package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject
import ru.marinovdev.controller.RegisterController

fun Application.configureRegisterRouting(hoconApplicationConfig: HoconApplicationConfig) {
    try {
        routing {
            post("/register") {
                val registerController by inject<RegisterController>(RegisterController::class.java)
                registerController.registerUser(call = call, hoconApplicationConfig = hoconApplicationConfig)
            }
        }
    } catch (e: Exception) {
        println("try catch configureRegisterRouting e=" + e)
    }
}