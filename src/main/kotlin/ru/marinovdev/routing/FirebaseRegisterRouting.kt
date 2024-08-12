package ru.marinovdev.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent
import ru.marinovdev.controller.FirebaseRegisterController

fun Application.configureFirebaseRegisterRouting() {
    try {
        routing {
            post("/firebase_register") {
                try {
                    val firebaseRegisterController by KoinJavaComponent.inject<FirebaseRegisterController>(FirebaseRegisterController::class.java)
                    firebaseRegisterController.saveUserFirebase(call)
                } catch (e: Exception) {
                    println(":::::::::::::::::: try catch 1 configureFirebaseRegisterRouting e=" + e)
                }
            }
        }
    } catch (e: Exception) {
        println(":::::::::::::::::: try catch 2 configureFirebaseRegisterRouting e=" + e)
    }
}