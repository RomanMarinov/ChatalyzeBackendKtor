package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent
import ru.marinovdev.controller.SignInController

fun Application.configureSignInRouting() {
    try {
        routing {
            post("/signin") {
                val signInController by KoinJavaComponent.inject<SignInController>(SignInController::class.java)
                signInController.performSignIn(
                    call
                )
            }
        }
    } catch (e: Exception) {
        println("try catch configureSignInRouting e=" + e)
    }
}