package ru.marinovdev.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.marinovdev.data.messages.model.ChatCompanion
import ru.marinovdev.data.user_manager.UserSocketManager
import ru.marinovdev.data.users_session.OnlineUserState
import ru.marinovdev.data.users_session.dto.MessageWrapper
import ru.marinovdev.domain.repository.MessageDataSourceRepository
import ru.marinovdev.domain.repository.UserSessionDataSourceRepository
import ru.marinovdev.model.MessageResponse

class SocketStateUserController(
    private val messageDataSourceRepository: MessageDataSourceRepository,
    private val userSessionDataSourceRepository: UserSessionDataSourceRepository,
) {
    val scope = CoroutineScope(Dispatchers.IO)
    var job: Job? = null

    suspend fun getStateUsersConnect() {
        if (job != null && job?.isActive == true) {
            println("Coroutine is already active")
            return
        }

        job?.cancelAndJoin() // Отменяем и ждем завершения предыдущей корутины
        job = scope.launch {
            while (isActive) {
                UserSocketManager.getAllUser().forEach {
                    checkUserSessionExists(userPhone = it.userPhone) // закидываю всех
                    delay(1L)
                }
                delay(5000L)
            }
        }
    }

    // проверяем равен ли userPhone в UserSocketConnectionEntity userPhone
    private fun checkUserSessionExists(userPhone: String) {
        userSessionDataSourceRepository.checkUserSessionExists(
            userPhone = userPhone,
            onSuccess = { userSessionExists ->
                if (userSessionExists) {
                    updateOnlineUserStateSession(userPhone = userPhone)
                } else {
                    insertOnlineUserStateSession(userPhone = userPhone)
                }
            },
            onFailure = { e ->
                println(":::::::::::::::::SocketMessageController checkUserSessionExists onFailure  e=" + e)
            }
        )
    }

    private fun updateOnlineUserStateSession(userPhone: String) {
        val online = "online"
        userSessionDataSourceRepository.updateUserSession(
            userPhone = userPhone,
            onlineOrOffline = online,
            onSuccess = {
              //  println(":::::::::::::::::SocketMessageController updateUserSession onSuccess")
                getListRecipientInDialogWithUserPhone(userPhone = userPhone)
            },
            onFailure = { e ->
                println(":::::::::::::::::SocketMessageController updateUserSession insertUserSession onFailure  e=" + e)
            }
        )
    }

    private fun insertOnlineUserStateSession(userPhone: String) {
        val online = "online"
        println(":::::::::::::::::SocketMessageController insertUserSession userPhone=" + userPhone)
        userSessionDataSourceRepository.insertUserSession(
            userPhone = userPhone,
            onlineOrOffline = online,
            onSuccess = {
                println(":::::::::::::::::SocketMessageController insertUserSession onSuccess")
                getListRecipientInDialogWithUserPhone(userPhone = userPhone)
            },
            onFailure = { e ->
                println(":::::::::::::::::SocketMessageController insertUserSession insertUserSession onFailure  e=" + e)
            }
        )
    }

    fun setOfflineUserStateSession(userPhone: String) {
        val offline = "offline"
        userSessionDataSourceRepository.updateUserSession(
            userPhone = userPhone,
            onlineOrOffline = offline,
            onSuccess = {
                println(":::::::::::::::::SocketStateUserController setOfflineUserState onSuccess")
            },
            onFailure = { e ->
                println(":::::::::::::::::SocketStateUserController updateUserSession onFailure  e=" + e)
            }
        )
    }

    private fun getListRecipientInDialogWithUserPhone(userPhone: String) {
        messageDataSourceRepository.getListRecipientInDialogWithUserPhone(
            userPhone = userPhone,
            onSuccess = { listRecipient ->
                //println(":::::::::::::::::SocketMessageController getListPairDialog onSuccess listRecipient=" + listRecipient)
                if (listRecipient.isNotEmpty()) {
                    getListOnlineOrOffline(listRecipient = listRecipient, userPhone = userPhone)
                }
            },
            onFailure = { e ->
                println(":::::::::::::::::SocketMessageController getListPairDialog onFailure  e=" + e)
            }
        )
    }

    private fun getListOnlineOrOffline(listRecipient: List<String>, userPhone: String) {
        userSessionDataSourceRepository.getListOnlineOrOffline(
            listRecipient = listRecipient,
            onSuccess = { listOnlineOrOffline ->
               // println(":::::::::::::::::SocketMessageController getListOnlineOrDate onSuccess  listOnlineOrDate=" + listOnlineOrOffline)
                sendPing(userPhone = userPhone, listOnlineUserState = listOnlineOrOffline)
            },
            onFailure = { e ->
                println(":::::::::::::::::SocketMessageController getListOnlineOrDate onFailure  e=" + e)
            }
        )
    }

    private fun sendPing(userPhone: String, listOnlineUserState: List<OnlineUserState>) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch(Dispatchers.IO) {
            UserSocketManager.getAllUser().forEach { userSocket ->
                if (userSocket.userPhone == userPhone) {
                    val listJson = Json.encodeToString(listOnlineUserState)
                    val listText = Json.encodeToString(
                        MessageWrapper(
                            type = "userList",
                            payloadJson = listJson
                        )
                    )
                    userSocket.socket.send(Frame.Text(listText))
                    return@forEach
                }
            }
        }
    }

    suspend fun saveChatCompanion(call: ApplicationCall) {
        try {
            val received = call.receive<ChatCompanion>()
            userSessionDataSourceRepository.updateUserSessionCompanion(
                senderPone = received.sender_phone,
                companionPhone = received.companion_phone,
                onSuccess = {
                    println(":::::::::::::::::SocketStateUserController saveChatCompanion onSuccess")
                    runBlocking {
                        println(":::::::::::SenderEmailController fetchUser onSuccess")
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.OK.value,
                                message = "The user chat companion has been updated"
                            )
                        )
                    }
                },
                onFailure = {
                    println(":::::::::::::::::SocketStateUserController saveChatCompanion onFailure")
                }
            )
        } catch (e: Exception) {
            println(":::::::::::::::::: try catch 2 saveChatCompanion e=" + e)
        }
    }
}