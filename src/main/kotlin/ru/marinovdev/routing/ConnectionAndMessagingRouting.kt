package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent
import ru.marinovdev.controller.SocketMessageController
import ru.marinovdev.controller.SocketStateUserController

fun Application.configureSocketConnectionAndMessagingRouting() {

    val socketMessageController by KoinJavaComponent.inject<SocketMessageController>(SocketMessageController::class.java)
    val socketStateUserController by KoinJavaComponent.inject<SocketStateUserController>(SocketStateUserController::class.java)
    install(Routing) {
        chatSocket(socketMessageController, socketStateUserController)
        getAllMessages(socketMessageController)
        getChats(socketMessageController)
        getStateUsersConnect(socketStateUserController)
        saveChatCompanion(socketStateUserController)
    }
}
