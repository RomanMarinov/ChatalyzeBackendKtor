package ru.marinovdev.features.send_email


import io.ktor.server.application.*
import io.ktor.server.routing.*


//fun Application.configureSenderEmailRouting() {
//    try {
//        routing {
//            post("/password") {
//                val senderEmailController = SenderEmailController(call)
//                senderEmailController.fetchAndSend()
//            }
//        }
//    } catch (e: Exception) {
//        println("try catch senderEmailRouting e=" + e)
//    }
//
//}
