package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent
import ru.marinovdev.controller.UserPasswordController

fun Application.configureForgotPasswordUserPasswordRouting(hoconApplicationConfig: HoconApplicationConfig) {
    routing {
        post("forgot_password/password") {
            val userPasswordController by KoinJavaComponent.inject<UserPasswordController>(UserPasswordController::class.java)
            userPasswordController.changePassword(call = call, hoconApplicationConfig = hoconApplicationConfig)
        }
    }
}