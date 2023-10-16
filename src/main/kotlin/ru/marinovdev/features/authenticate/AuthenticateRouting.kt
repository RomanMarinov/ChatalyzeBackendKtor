//package ru.marinovdev.features.authenticate
//
//import io.ktor.server.application.*
//import io.ktor.server.auth.*
//import io.ktor.server.routing.*
//
//
//fun Application.configureAuthenticate() {
//    routing {
//        authenticate {
//            get("authenticate") {
//                val authenticateController = AuthenticateController(call = call)
//                authenticateController.executeAuthenticate()
//            }
//        }
//    }
//}