package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent
import ru.marinovdev.controller.UpdateTokensController

fun Application.configureUpdateTokensRouting() {
    try {
        routing {
            post("/update_two_token") {
                val updateTokensController by KoinJavaComponent.inject<UpdateTokensController>(UpdateTokensController::class.java)
                updateTokensController.execute(call)
            }
        }
    } catch (e: Exception) {
        println(":::::::::::::::try catch configureUpdateTokensRouting e=" + e)
    }
}