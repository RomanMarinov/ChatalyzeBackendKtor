package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject
import ru.marinovdev.controller.RegisterController

fun Application.configureRegisterRouting() {
    try {
        routing {
            post("/register") {
                val registerController by inject<RegisterController>(RegisterController::class.java)
                registerController.registerUser(call)
            }
        }
    } catch (e: Exception) {
        println("try catch configureRegisterRouting e=" + e)
    }
}