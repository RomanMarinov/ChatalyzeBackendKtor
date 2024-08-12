package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject
import ru.marinovdev.controller.FirebaseCommandController

fun Application.configureFirebaseCommandRouting() {
    try {
        routing {
            post("/firebase_command") {
                try {
                    val firebaseCommandController by inject<FirebaseCommandController>(FirebaseCommandController::class.java)
                    firebaseCommandController.executeDivideCommands(call)
                } catch (e: Exception) {
                    println(":::::::::::::::::: try catch 1 configureFirebaseRouting e="+ e)
                }
            }
        }
    } catch (e: Exception) {
        println(":::::::::::::::::: try catch 2 configureFirebaseRouting e="+ e)
    }
}