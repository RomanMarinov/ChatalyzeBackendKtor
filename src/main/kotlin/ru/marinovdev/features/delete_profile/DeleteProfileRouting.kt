package ru.marinovdev.features.delete_profile

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.marinovdev.features.auth_lackner.security.token.JwtTokenService

fun Application.configureDeleteProfileRouting(jwtTokenService: JwtTokenService) {
    routing {
        post("delete_profile") {
            val deleteProfileController = DeleteProfileController(call, jwtTokenService = jwtTokenService)
            deleteProfileController.delete()
        }
    }
}