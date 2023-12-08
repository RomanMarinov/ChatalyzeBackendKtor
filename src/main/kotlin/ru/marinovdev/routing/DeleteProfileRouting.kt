package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent
import ru.marinovdev.controller.DeleteProfileController

fun Application.configureDeleteProfileRouting() {
    try {
        routing {
            post("/delete_profile") {
                val deleteProfileController by KoinJavaComponent.inject<DeleteProfileController>(DeleteProfileController::class.java)
                deleteProfileController.delete(call)
            }
        }
    } catch (e: Exception) {
        println("try catch configureDeleteProfileRouting e=" + e)
    }

}