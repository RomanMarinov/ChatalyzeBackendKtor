package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent
import ru.marinovdev.controller.UserCodeController

fun Application.configureForgotPasswordUserCodeRouting() {
    routing {
        post("forgot_password/code") {
            val userCodeController by KoinJavaComponent.inject<UserCodeController>(UserCodeController::class.java)
            userCodeController.execute(call)
        }
    }
}