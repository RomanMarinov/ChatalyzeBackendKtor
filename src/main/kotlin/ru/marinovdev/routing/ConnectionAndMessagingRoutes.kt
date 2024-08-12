package ru.marinovdev.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
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
import ru.marinovdev.data.user_manager.UserSocketManager

fun Route.chatSocket(
    socketMessageController: SocketMessageController,
    socketStateUserController: SocketStateUserController
) {
    webSocket("/chatsocket") {

        val session = call.sessions.get<ChatSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
            return@webSocket
        }

        try {
            if (UserSocketManager.getAllUser().isEmpty()) {
                socketStateUserController.getStateUsersConnect()
            }

            socketMessageController.onJoin(
                userPhone = session.sender,
                sessionId = session.sessionId,
                socket = this
            )

            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    println(":::::::::::::::::Route.chatSocket frame=" + frame.readText())
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
        } finally { // выполняется в любом случае
            socketMessageController.tryDisconnect(userPhone = session.sender)
        }
    }
}

fun Route.getAllMessages(socketMessageController: SocketMessageController) {
    authenticate("jwt") {
        post("/messages") {
            socketMessageController.getAllMessages(call = call)
        }
    }
}

fun Route.getChats(socketMessageController: SocketMessageController) {
    authenticate("jwt") {
        post("/chats") {
            socketMessageController.execute(call = call)
        }
    }
}

fun Route.getStateUsersConnect(socketStateUserController: SocketStateUserController) {
    get("/state_user_connection") {
    }
}

// auth not required
fun Route.saveChatCompanion(socketStateUserController: SocketStateUserController) {
    try {
        post ("/chat_companion") {
            socketStateUserController.saveChatCompanion(call = call)
        }
    } catch (e: Exception) {
        println(":::::::::::::::::: try catch 1 saveChatCompanion e=" + e)
    }
}