package ru.marinovdev.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
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

// это момент когда я решил переделать под себя
class SocketMessageController(
    private val messageDataSourceRepository: MessageDataSourceRepository
) {
//    private val members = ConcurrentHashMap<String, Member>()

    suspend fun onJoin(userPhone: String, sessionId: String, socket: WebSocketSession) {
//        if (members.containsKey(userPhone)) {
//            println(":::::::::::::::::::::::MemberAlreadyExistsException()")
//            throw MemberAlreadyExistsException()
//        }
//        members[userPhone] = Member(
//            username = userPhone,
//            sessionId = sessionId,
//            socket = socket
//        )

        /////////////////////////// моя новая добавление
        val userAlreadyExists = UserSocketManager.checkContainsUser(userPhone = userPhone)
        if (userAlreadyExists) {
            UserSocketManager.removeMemberByUserPhone(userPhone = userPhone)
        } else {
            UserSocketManager.addUser(
                userPhone = userPhone,
                sessionId = sessionId,
                socket = socket
            )
        }
    }

    /////////////////////////////////
    // метод сработает когда клиент отправит сообщение
    // тут надо сделать сразу вставку в бд объект отправителя получателя и текст
    suspend fun sendMessage(senderUsername: String, message: String) {
        try {
            val jsonObject = Json.decodeFromString<JsonObject>(message)
            val sender = jsonObject["sender"].toString().replace("\"", "")
            val recipient = jsonObject["recipient"].toString().replace("\"", "")
            val textMessage = jsonObject["textMessage"].toString().replace("\"", "")

            // напимер тут я сделаю вставку в бд, далее мы запишем сообщения в бд. Это
            val messageEntity = Message(
                sender = sender,
                recipient = recipient,
                textMessage = textMessage,
                createdAt = ""
            )
            messageDataSourceRepository.insertMessage(messageEntity)

            try {
//                // отправка получателю
//                UserSocketManager.getAllUser().forEach { member ->
//                    println(":::::::::::::::::RoomController members.values.forEach member.username=" + member.userPhone)
//                    println(":::::::::::::::::RoomController members.values.forEach recipient=" + recipient)
//                    if (member.userPhone == recipient) {
//                        println(":::::::::::::::::RoomController member.username=" + member.userPhone + " toUser=" + recipient)
//                        val messageObject = Message(
//                            sender = senderUsername,
//                            recipient = recipient,
//                            textMessage = textMessage,
//                            createdAt = ""
//                        )
//                        // далее мы запишем сообщение в бд
//                        // messageDataSource.insertMessage(messageEntity)
//
//
//                        // просто для теста взять полед сообщенеи
//                        val lastMessage = messageDataSourceRepository.getLastMessage(
//                            sender = sender,
//                            recipient = recipient,
//                            onFailure = { e ->
//                                println(":::::::::::::::::SocketMessageController getLastMessage e=" + e)
//                            }
//                        )
//
//
//
//                        val parsedMessage = Json.encodeToString(lastMessage)
//                        member.socket.send(Frame.Text(parsedMessage))
//                    }
//                }
            } catch (e: Exception) {
                println(":::::::::::::::::try catch SocketMessageController отправка получателю e=" + e)
            }


            try {
                // отправка отправителю

                // пока вижу косяк в том что выполняется два раза ember.username == sender
                // надо посмотреть все эелемнты UserSocketManager.getAllUser()
                // и узнать как так получается
                println(":::::::::::::::UserSocketManager.отправка отправителю")
                println(":::::::::::::::UserSocketManager.getAllUser().size=" + UserSocketManager.getAllUser().size)
                UserSocketManager.getAllUser().forEach {
                    println(":::::::::::::::UserSocketManager it.socket=" + it.socket)
                    println(":::::::::::::::UserSocketManager it.username=" + it.userPhone)
                    println(":::::::::::::::UserSocketManager it.sessionId=" + it.sessionId)
                }

                UserSocketManager.getAllUser().forEach { member ->
                    if (member.userPhone == sender) {
                        println(":::::::::::::::::RoomController member.username=" + member.userPhone + " toUser=" + recipient)
                        val messageObject = Message(
                            sender = senderUsername,
                            recipient = recipient,
                            textMessage = textMessage,
                            createdAt = ""
                        )

                        // далее мы запишем сообщение в бд
                        // messageDataSource.insertMessage(messageEntity)

                        // просто для теста взять полед сообщенеи
                        val lastMessage = messageDataSourceRepository.getLastMessage(
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

    suspend fun tryDisconnect(userPhone: String) {
        println("::::::::::::::::::::tryDisconnect userPhone=" + userPhone)

//        members[username]?.socket?.close()
//        if (members.containsKey(username)) {
//            UserSocketManager.deleteUser(username = username)
//            members.remove(username)
//        }

        UserSocketManager.getUserSocket(userPhone = userPhone)?.close()
        if (UserSocketManager.checkContainsUser(userPhone = userPhone)) {
         UserSocketManager.removeMemberByUserPhone(userPhone = userPhone)
        }
    }

    suspend fun getChats(call: ApplicationCall) {
        val receivedSenderParam = call.receive<Sender>()

        messageDataSourceRepository.getChats(
            receivedSender = receivedSenderParam.sender,
            onSuccess = { listChat ->
                runBlocking {
                    call.respond(
                        HttpStatusCode.OK,
                        listChat
                    )
                }
            },
            onFailure = {

            }
        )
    }

    suspend fun newMethod(call: ApplicationCall, webSocketServerSession: DefaultWebSocketServerSession) {
//        val session = call.sessions.get<ChatSession>()
//        // Route.chatSocket session=ChatSession(username=89303493563, sessionId=cf7649c162989855)
//        println(":::::::::::::::::Route.chatSocket session=" + session)
//        if (session == null) {
//            webSocketServerSession.close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
//            return
//        }
        ///////////////////////
        // как только подключается юзер, я ищу этого юзера в таблице users_session
        // если он там есть то я перезаписываю его
        // запускается цикл в 5 сек в котором
        // я получаю список номеров с кем юзер в диалоге
        // далее проверяю у кого соединение активно и у кого не активно (время дисконнекта)
        // и рассылаю этот список активным юзерам в виде сообщения типа пинг

//        GlobalScope.launch {
//            while (true) {
//                delay(5000L)  // Отправлять сообщение PING каждые 5 секунд.
//                val yourData: ByteArray = "your message".toByteArray(Charsets.UTF_8) // замените это на свои данные
//                this@webSocket.outgoing.send(Frame.Ping(yourData))
//                println(":::::::::::::::::Route.chatSocket server sends PING")
//            }
//        }
        ///////////////////////
//        try {
//            println(":::::::::::::::::Route.chatSocket 1")
//            onJoin(
//                userPhone = session.sender,
//                sessionId = session.sessionId,
//                socket = webSocketServerSession
//            )
//
//            // Set up ping-pong interval
////            val pingJob = launch {
////                while (isActive) {
////                    send(Frame.Ping("Ping еп тить".toByteArray())) // Отправляем ping-фрейм
////                    delay(1000L)
////                }
////            }
////            pingJob.start()
//
//            println(":::::::::::::::::Route.chatSocket 2")
//            // далее эта часть кода сработает если клиент отпарвить новый фрейм
//            webSocketServerSession.incoming.consumeEach { frame ->
//                println(":::::::::::::::::Route.chatSocket frame=" + frame)
//                if (frame is Frame.Text) {
//                    sendMessage(
//                        senderUsername = session.sender,
//                        message = frame.readText()
//                    )
//                }
//            }
//        } catch (e: MemberAlreadyExistsException) {
//            call.respond(HttpStatusCode.Conflict)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            tryDisconnect(session.sender)
//        }
    }
}





//// это момент когда я решил переделать под себя
//class SocketMessageController(
//    private val messageDataSourceRepository: MessageDataSourceRepository,
////    private val userSessionDataSourceRepository: UserSessionDataSourceRepository
//) {
//    private val members = ConcurrentHashMap<String, Member>()
//
//    fun onJoin(userPhone: String, sessionId: String, socket: WebSocketSession) {
//        if (members.containsKey(userPhone)) {
//            println(":::::::::::::::::::::::MemberAlreadyExistsException()")
//            throw MemberAlreadyExistsException()
//        }
//        members[userPhone] = Member(
//            username = userPhone,
//            sessionId = sessionId,
//            socket = socket
//        )
//
//        /////////////////////////// моя новая добавление
//        val userAlreadyExists = UserSocketManager.checkContainsUser(userPhone = userPhone)
//        if (userAlreadyExists) {
//
//        } else {
//            UserSocketManager.addUserSocket(
//                userPhone = userPhone,
//                sessionId = sessionId,
//                socket = socket
//            )
//        }
//        ////////////////////////
//
//
//    }
//
//    /////////////////////////////////
//    // метод сработает когда клиент отправит сообщение
//    // тут надо сделать сразу вставку в бд объект отправителя получателя и текст
//    suspend fun sendMessage(senderUsername: String, message: String) {
//
//        val jsonObject = Json.decodeFromString<JsonObject>(message)
//        val sender = jsonObject["sender"].toString().replace("\"", "")
//        val recipient = jsonObject["recipient"].toString().replace("\"", "")
//        val textMessage = jsonObject["textMessage"].toString()
//
//        // напимер тут я сделаю вставку в бд, далее мы запишем сообщения в бд. Это
//        val messageEntity = Message(
//            sender = sender,
//            recipient = recipient,
//            textMessage = textMessage,
//            createdAt = ""
//        )
//        messageDataSourceRepository.insertMessage(messageEntity)
//
//        members.values.forEach { member -> // тут мне надо отправить сообщение моему получателю
//            println(":::::::::::::::::RoomController members.values.forEach member.username=" + member.username)
//            println(":::::::::::::::::RoomController members.values.forEach recipient=" + recipient)
//            if (member.username == recipient) {
//                println(":::::::::::::::::RoomController member.username=" + member.username + " toUser=" + recipient)
//                val messageObject = Message(
//                    sender = senderUsername,
//                    recipient = recipient,
//                    textMessage = textMessage,
//                    createdAt = ""
//                )
//                // далее мы запишем сообщение в бд
//                // messageDataSource.insertMessage(messageEntity)
//
//
//                // просто для теста взять полед сообщенеи
//                val lastMessage = messageDataSourceRepository.getLastMessage(
//                    sender = sender,
//                    recipient = recipient,
//                    onFailure = { e ->
//                        println(":::::::::::::::::SocketMessageController getLastMessage e=" + e)
//                    }
//                )
//                val parsedMessage = Json.encodeToString(lastMessage)
//                member.socket.send(Frame.Text(parsedMessage))
//            }
//        }
//
//        members.values.forEach { member -> // тут мне надо отправить сообщение моему получателю
//            if (member.username == sender) {
//                println(":::::::::::::::::RoomController member.username=" + member.username + " toUser=" + recipient)
//                val messageObject = Message(
//                    sender = senderUsername,
//                    recipient = recipient,
//                    textMessage = textMessage,
//                    createdAt = ""
//                )
//
//                // далее мы запишем сообщение в бд
//                // messageDataSource.insertMessage(messageEntity)
//
//                // просто для теста взять полед сообщенеи
//                val lastMessage = messageDataSourceRepository.getLastMessage(
//                    sender = sender,
//                    recipient = recipient,
//                    onFailure = { e ->
//                        println(":::::::::::::::::SocketMessageController getLastMessage e=" + e)
//                    }
//                )
//
//                val parsedMessage = Json.encodeToString(lastMessage)
//                member.socket.send(Frame.Text(parsedMessage))
//            }
//        }
//    }
//
//    suspend fun getAllMessages(call: ApplicationCall) {
//        val receivedUserPairChat = call.receive<UserPairChat>()
//
//        messageDataSourceRepository.getAllMessages(
//            userPairChat = receivedUserPairChat,
//            onSuccess = { listMessage ->
//                runBlocking {
//                    call.respond(
//                        HttpStatusCode.OK,
//                        listMessage
//                    )
//                }
//            },
//            onFailure = {
//
//            }
//        )
//    }
//
//    suspend fun tryDisconnect(username: String) {
//        println("::::::::::::::::::::tryDisconnect")
//        members[username]?.socket?.close()
//        if (members.containsKey(username)) {
//            UserSocketManager.deleteUserSocket(username = username)
//            members.remove(username)
//        }
//    }
//
//    suspend fun getChats(call: ApplicationCall) {
//        val receivedSenderParam = call.receive<Sender>()
//
//        messageDataSourceRepository.getChats(
//            receivedSender = receivedSenderParam.sender,
//            onSuccess = { listChat ->
//                runBlocking {
//                    call.respond(
//                        HttpStatusCode.OK,
//                        listChat
//                    )
//                }
//            },
//            onFailure = {
//
//            }
//        )
//    }
//
//    suspend fun newMethod(call: ApplicationCall, webSocketServerSession: DefaultWebSocketServerSession) {
//        val session = call.sessions.get<ChatSession>()
//        // Route.chatSocket session=ChatSession(username=89303493563, sessionId=cf7649c162989855)
//        println(":::::::::::::::::Route.chatSocket session=" + session)
//        if (session == null) {
//            webSocketServerSession.close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
//            return
//        }
//        ///////////////////////
//        // как только подключается юзер, я ищу этого юзера в таблице users_session
//        // если он там есть то я перезаписываю его
//        // запускается цикл в 5 сек в котором
//        // я получаю список номеров с кем юзер в диалоге
//        // далее проверяю у кого соединение активно и у кого не активно (время дисконнекта)
//        // и рассылаю этот список активным юзерам в виде сообщения типа пинг
//
////        GlobalScope.launch {
////            while (true) {
////                delay(5000L)  // Отправлять сообщение PING каждые 5 секунд.
////                val yourData: ByteArray = "your message".toByteArray(Charsets.UTF_8) // замените это на свои данные
////                this@webSocket.outgoing.send(Frame.Ping(yourData))
////                println(":::::::::::::::::Route.chatSocket server sends PING")
////            }
////        }
//        ///////////////////////
//        try {
//            println(":::::::::::::::::Route.chatSocket 1")
//            onJoin(
//                userPhone = session.sender,
//                sessionId = session.sessionId,
//                socket = webSocketServerSession
//            )
//
//            // Set up ping-pong interval
////            val pingJob = launch {
////                while (isActive) {
////                    send(Frame.Ping("Ping еп тить".toByteArray())) // Отправляем ping-фрейм
////                    delay(1000L)
////                }
////            }
////            pingJob.start()
//
//            println(":::::::::::::::::Route.chatSocket 2")
//            // далее эта часть кода сработает если клиент отпарвить новый фрейм
//            webSocketServerSession.incoming.consumeEach { frame ->
//                println(":::::::::::::::::Route.chatSocket frame=" + frame)
//                if (frame is Frame.Text) {
//                    sendMessage(
//                        senderUsername = session.sender,
//                        message = frame.readText()
//                    )
//                }
//            }
//        } catch (e: MemberAlreadyExistsException) {
//            call.respond(HttpStatusCode.Conflict)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            tryDisconnect(session.sender)
//        }
//    }
//}