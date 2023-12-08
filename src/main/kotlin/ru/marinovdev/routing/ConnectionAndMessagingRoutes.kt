package ru.marinovdev.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import ru.marinovdev.controller.SocketMessageController
import ru.marinovdev.controller.SocketStateUserController
import ru.marinovdev.data.socket_connection.ChatSession
import ru.marinovdev.data.socket_connection.MemberAlreadyExistsException

fun Route.chatSocket(socketMessageController: SocketMessageController) {
    webSocket("/chatsocket") {

        //socketMessageController.newMethod(call = call, webSocketServerSession = this)
////////////////////
        val session = call.sessions.get<ChatSession>()
        // Route.chatSocket session=ChatSession(username=89303493563, sessionId=cf7649c162989855)
        println(":::::::::::::::::Route.chatSocket session=" + session)
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
            return@webSocket
        }

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
        try {
            println(":::::::::::::::::Route.chatSocket 1")
            socketMessageController.onJoin(
                userPhone = session.sender,
                sessionId = session.sessionId,
                socket = this
            )

            // Set up ping-pong interval
//            val pingJob = launch {
//                while (isActive) {
//                    send(Frame.Ping("Ping еп тить".toByteArray())) // Отправляем ping-фрейм
//                    delay(1000L)
//                }
//            }
//            pingJob.start()

            println(":::::::::::::::::Route.chatSocket 2")
            // далее эта часть кода сработает если клиент отпарвить новый фрейм
            incoming.consumeEach { frame ->
                println(":::::::::::::::::Route.chatSocket frame=" + frame)
                if (frame is Frame.Text) {
                    socketMessageController.sendMessage(
                        senderUsername = session.sender,
                        message = frame.readText()
                    )
                }
            }
        } catch (e: MemberAlreadyExistsException) {
            call.respond(HttpStatusCode.Conflict)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            socketMessageController.tryDisconnect(session.sender)
        }
    }
}

fun Route.getAllMessages(socketMessageController: SocketMessageController) {
    println(":::::::::::::::::Route.getAllMessages")
    post("/messages") {
        socketMessageController.getAllMessages(call = call)
    }
}

fun Route.getChats(socketMessageController: SocketMessageController) {
    post("/chats") {
        socketMessageController.getChats(call = call)
    }
}

fun Route.getStateUsersConnect(socketStateUserController: SocketStateUserController) {
    get("/state_user_connection") {
        println(":::::::::::::::::Route.getStateUsersConnect")
        socketStateUserController.getStateUsersConnect(call = call)
    }
}
