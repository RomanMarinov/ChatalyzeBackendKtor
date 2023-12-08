package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent
import ru.marinovdev.controller.UserEmailController

fun Application.configureForgotPasswordUserEmailRouting() {
    try {
        routing {
            post("forgot_password/email") {
                val userEmailController by KoinJavaComponent.inject<UserEmailController>(UserEmailController::class.java)
                userEmailController.fetchAndSend(call)
            }
        }
    } catch (e: Exception) {
        println(":::::::::::try catch configureForgotPasswordUserEmailRouting e=$e")
    }

}