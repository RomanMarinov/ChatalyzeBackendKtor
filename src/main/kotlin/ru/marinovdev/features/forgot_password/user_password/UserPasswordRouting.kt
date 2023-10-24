package ru.marinovdev.features.forgot_password.user_password

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.marinovdev.features.auth_lackner.security.hashing_password.SHA256HashingService

fun Application.configureForgotPasswordUserPasswordRouting(hashingService: SHA256HashingService) {
    routing {
        post("forgot_password/password") {
            val userPasswordController = UserPasswordController(call, hashingService = hashingService)
            userPasswordController.changePassword()
        }
    }
}