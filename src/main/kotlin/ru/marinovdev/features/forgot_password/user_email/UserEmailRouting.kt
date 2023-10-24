package ru.marinovdev.features.forgot_password.user_email

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.marinovdev.features.auth_lackner.security.hashing_code.SHA256HashingCodeService

fun Application.configureForgotPasswordUserEmailRouting(
    hashingCodeService: SHA256HashingCodeService
) {
    try {
        routing {
            post("forgot_password/email") {
                val userEmailController = UserEmailController(
                    call,
                    hashingCodeService = hashingCodeService
                )
                userEmailController.fetchAndSend()
            }
        }
    } catch (e: Exception) {
        println(":::::::::::try catch configureForgotPasswordUserEmailRouting e=$e")
    }

}