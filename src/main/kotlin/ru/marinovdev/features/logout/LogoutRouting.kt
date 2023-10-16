package ru.marinovdev.features.logout

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLogoutRouting() {
    try {
        routing {
            post("/logout") {
                val logoutController = LogoutController(call)
                logoutController.logoutUser()
            }
        }
    } catch (e: Exception) {
        println("try catch logout e=" + e)
    }

}