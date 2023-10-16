//package ru.marinovdev.features.secret
//
//import io.ktor.server.application.*
//import io.ktor.server.routing.*
//
//fun Application.configureSecret() {
//    routing {
//        get("secret") {
//            val secretController = SecretController(call = call)
//            secretController.getSecretInfo()
//        }
//    }
//}