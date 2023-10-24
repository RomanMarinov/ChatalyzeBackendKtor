package ru.marinovdev.features.forgot_password.user_code

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.marinovdev.features.auth_lackner.security.hashing_code.SHA256HashingCodeService

fun Application.configureForgotPasswordUserCodeRouting(hashingCodeService: SHA256HashingCodeService) {
    routing {
        post("forgot_password/code") {
            val userCodeController = UserCodeController(call, hashingCodeService = hashingCodeService)
            userCodeController.execute()
        }
    }
}