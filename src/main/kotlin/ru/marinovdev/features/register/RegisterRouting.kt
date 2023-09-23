package ru.marinovdev.features.register


import io.ktor.server.application.*
import io.ktor.server.routing.*


fun Application.configureRegisterRouting() {
    try {
        routing {
            post("/register") {
                val registerController = RegisterController(call)
                registerController.registerNewUser()
            }
        }
    } catch (e: Exception) {
        println("try catch e=" + e)
    }

}
