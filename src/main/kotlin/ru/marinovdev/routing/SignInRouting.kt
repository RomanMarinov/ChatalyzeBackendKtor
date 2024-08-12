package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent
import ru.marinovdev.controller.SignInController

fun Application.configureSignInRouting(hoconApplicationConfig: HoconApplicationConfig) {
    try {
        routing {
            post("/signin") {
                val signInController by KoinJavaComponent.inject<SignInController>(SignInController::class.java)
                signInController.performSignIn(
                    call = call, hoconApplicationConfig = hoconApplicationConfig
                )
            }
        }
    } catch (e: Exception) {
        println("try catch configureSignInRouting e=" + e)
    }
}