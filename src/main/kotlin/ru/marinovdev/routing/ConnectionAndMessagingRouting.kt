package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.java.KoinJavaComponent
import ru.marinovdev.controller.SocketMessageController
import ru.marinovdev.controller.SocketStateUserController

@Serializable
data class Test(
    val text: String
)

fun Application.configureSocketConnectionAndMessagingRouting() {
//    routing {
//        get("/") {
//            // `call.respond` сериализует объект `Test` в JSON и отправляет ответ клиенту.
//            call.respond(Test(text = "hi"))
//        }
//    }


    ////////////////////////////////////
    val socketMessageController by KoinJavaComponent.inject<SocketMessageController>(SocketMessageController::class.java)
    val socketStateUserController by KoinJavaComponent.inject<SocketStateUserController>(SocketStateUserController::class.java)
    install(Routing) {
        chatSocket(socketMessageController)
        getAllMessages(socketMessageController)
        getChats(socketMessageController)
        getStateUsersConnect(socketStateUserController)
    }

    /////////////////////////////////////


}
