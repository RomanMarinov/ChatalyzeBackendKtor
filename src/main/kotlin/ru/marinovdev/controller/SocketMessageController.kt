package ru.marinovdev.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import ru.marinovdev.data.messages.model.Message
import ru.marinovdev.data.messages.model.Sender
import ru.marinovdev.data.messages.model.UserPairChat
import ru.marinovdev.data.user_manager.UserSocketManager
import ru.marinovdev.data.users_session.dto.MessageWrapper
import ru.marinovdev.domain.repository.MessageDataSourceRepository
import ru.marinovdev.domain.repository.UserSessionDataSourceRepository

class SocketMessageController(
    private val messageDataSourceRepository: MessageDataSourceRepository,
    private val socketStateUserController: SocketStateUserController,
    private val userSessionDataSourceRepository: UserSessionDataSourceRepository
) {
     fun onJoin(userPhone: String, sessionId: String, socket: WebSocketSession) {
        if (!UserSocketManager.checkContainsUser(userPhone = userPhone)) {
            UserSocketManager.addUser(
                userPhone = userPhone,
                sessionId = sessionId,
                socket = socket
            )
        }
    }

    suspend fun sendMessage(senderUsername: String, message: String) {   // метод сработает когда клиент отправит сообщение
        try {
            val jsonObject = Json.decodeFromString<JsonObject>(message)
            val sender = jsonObject["sender"].toString().replace("\"", "")
            val recipient = jsonObject["recipient"].toString().replace("\"", "")
            val textMessage = jsonObject["textMessage"].toString().replace("\"", "")

            val messageEntity = Message(
                sender = sender,
                recipient = recipient,
                textMessage = textMessage,
                createdAt = ""
            )
            messageDataSourceRepository.insertMessage(messageEntity)

            try {
                // отправка ПОЛУЧАТЕЛЮ это сработает только если
                // юзер не закрыл приложение
                UserSocketManager.getAllUser().forEach { member ->
                    var lastMessage: Message? = null

                    if (member.userPhone == recipient) {
                        lastMessage = messageDataSourceRepository.getLastMessage(
                            sender = sender,
                            recipient = recipient,
                            onFailure = { e ->
                                println(":::::::::::::::::SocketMessageController getLastMessage e=" + e)
                            }
                        )
                        delay(10L)

                        userSessionDataSourceRepository.getUserSessionCompanion(
                            senderPhone = sender,
                            companionPhone = recipient,
                            onSuccess = { alreadyExistsCompanionPhone ->

                                if (alreadyExistsCompanionPhone) { // сейчас в чате
                                    val parsedMessage = Json.encodeToString(lastMessage)
                                    val messageText = Json.encodeToString(
                                        MessageWrapper(
                                            type = "singleMessage",
                                            payloadJson = parsedMessage
                                        )
                                    )
                                    println(":::::::::::::::::SocketMessageController отправка получателю по сокету=" + messageText)
                                    runBlocking {
                                        member.socket.send(Frame.Text(messageText))
                                    }
                                } else { // сейчас не в чате (отправить пуш)
                                    val parsedMessage = Json.encodeToString(lastMessage)
                                    val messageText = Json.encodeToString(
                                        MessageWrapper(
                                            type = "companionOffline",
                                            payloadJson = parsedMessage
                                        )
                                    )
                                    println(":::::::::::::::::SocketMessageController отправка получателю по push=" + messageText)
                                    runBlocking {
                                        member.socket.send(Frame.Text(messageText))
                                    }
                                }
                                return@getUserSessionCompanion
                            },
                            onFailure = { e ->
                                println(":::::::::::::::::try catch SocketMessageController e=" + e)
                            }
                        )

                        return@forEach
                    }

                    val userPhones = UserSocketManager.getAllUser().map { it.userPhone }.toSet()

                    if (recipient !in userPhones) {
                        lastMessage = messageDataSourceRepository.getLastMessage(
                            sender = sender,
                            recipient = recipient,
                            onFailure = { e ->
                                println(":::::::::::::::::SocketMessageController getLastMessage e=" + e)
                            }
                        )
                        val parsedMessage = Json.encodeToString(lastMessage)
                        val messageText = Json.encodeToString(
                            MessageWrapper(
                                type = "companionOffline",
                                payloadJson = parsedMessage
                            )
                        )
                        println(":::::::::::::::::SocketMessageController отправка получателю по push=" + messageText)
                        runBlocking {
                            member.socket.send(Frame.Text(messageText))
                        }
                    }
                }
            } catch (e: Exception) {
                println(":::::::::::::::::try catch SocketMessageController отправка получателю e=" + e)
            }

            try {
                println(":::::::::::::::UserSocketManager.отправка отправителю")
                UserSocketManager.getAllUser().forEach { member ->
                    if (member.userPhone == sender) {

                        val lastMessage: Message? = messageDataSourceRepository.getLastMessage(
                            sender = sender,
                            recipient = recipient,
                            onFailure = { e ->
                                println(":::::::::::::::::SocketMessageController getLastMessage e=" + e)
                            }
                        )
                        delay(10L)

                        val parsedMessage = Json.encodeToString(lastMessage)
                        val messageText = Json.encodeToString(
                            MessageWrapper(
                                type = "singleMessage",
                                payloadJson = parsedMessage
                            )
                        )
                        println(":::::::::::::::::SocketMessageController отправка отправителю=" + messageText)
                        member.socket.send(Frame.Text(messageText))
                        return@forEach
                    }
                }
            } catch (e: Exception) {
                println(":::::::::::::::::try catch SocketMessageController отправка отправителю e=" + e)
            }
        } catch (e: Exception) {
            println(":::::::::::::::::try catch SocketMessageController sendMessage e=" + e)
        }

    }

    suspend fun getAllMessages(call: ApplicationCall) {
        val receivedUserPairChat = call.receive<UserPairChat>()

        messageDataSourceRepository.getAllMessages(
            userPairChat = receivedUserPairChat,
            onSuccess = { listMessage ->
                runBlocking {
                    call.respond(
                        HttpStatusCode.OK,
                        listMessage
                    )
                }
            },
            onFailure = {

            }
        )
    }

    fun tryDisconnect(userPhone: String) {
        println("::::::::::::::::::::tryDisconnect userPhone=" + userPhone)

        socketStateUserController.setOfflineUserStateSession(userPhone = userPhone)

        if (UserSocketManager.checkContainsUser(userPhone = userPhone)) {
            println("::::::::::::::::::::tryDisconnect checkContainsUser")
            UserSocketManager.removeMemberByUserPhone(userPhone = userPhone)
        }
        println(" tryDisconnect userPhone кто остался на рассылку=" + UserSocketManager.getAllUser())
    }

    suspend fun execute(call: ApplicationCall) {
        val senderReceived = call.receive<Sender>()
        getChats(call = call, senderReceived = senderReceived)
    }

    private suspend fun getChats(call: ApplicationCall, senderReceived: Sender) {
        messageDataSourceRepository.getChats(
            receivedSender = senderReceived.sender,
            onSuccess = { listChat ->
                runBlocking {
                    call.respond(
                        HttpStatusCode.OK,
                        listChat
                    )
                }
            },
            onFailure = { e ->
                println(":::::::::::::::::try catch getChats e=" + e)
            }
        )
    }
}