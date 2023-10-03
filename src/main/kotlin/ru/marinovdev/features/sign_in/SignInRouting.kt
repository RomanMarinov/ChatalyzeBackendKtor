package ru.marinovdev.features.sign_in

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureSignInRouting() {
    routing {
        post("/signin") {
            val signInController = SignInController(call)
            signInController.performSignIn()
        }
    }
}