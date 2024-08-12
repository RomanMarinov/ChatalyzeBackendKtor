package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject
import ru.marinovdev.controller.LogoutController

fun Application.configureLogoutRouting() {
    try {
        routing {
                post("/logout") {
                    val logoutController by inject<LogoutController>(LogoutController::class.java)
                    logoutController.logoutUser(call)
                }
        }
    } catch (e: Exception) {
        println("try catch configureLogoutRouting e=" + e)
    }

}