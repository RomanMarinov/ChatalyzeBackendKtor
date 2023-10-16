package ru.marinovdev.features.register

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.marinovdev.features.auth_lackner.security.hashing.HashingService

fun Application.configureRegisterRouting(hashingService: HashingService) {
    try {
        routing {
            post("/register") {
                val registerController = RegisterController(call, hashingService = hashingService)
                registerController.registerUser()

            }
        }
    } catch (e: Exception) {
        println("try catch register e=" + e)
    }
}


//fun Application.configureRegisterRouting() {
//    try {
//        routing {
//            post("/register") {
//                val registerController = RegisterController(call)
//                registerController.registerUser()
//
//            }
//        }
//    } catch (e: Exception) {
//        println("try catch e=" + e)
//    }
//}
