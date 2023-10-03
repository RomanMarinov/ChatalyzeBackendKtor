package ru.marinovdev.features.register


import io.ktor.server.application.*
import io.ktor.server.routing.*


fun Application.configureRegisterRouting() {
    try {
        routing {
            post("/register") {
                val registerController = RegisterController(call)
                registerController.registerNewUser()

//                println(":::::::::::insertTokenIntoDataBaseAndSend onSuccess")
//                val registerResponseRemoteJson = Json.encodeToString(RegisterResponseRemote(token = "mytoken"))
//
//                        // отправляю клиенту сгенеренный токен чтобы он хранил его у себя и при входе чекался
//                        println(":::::::::::call.respond 5")
//                        call.respond(
//                            MessageResponse(
//                                HttpStatusCode.OK.value,
//                                message = registerResponseRemoteJson
//                            )
//                        )

            }
        }
    } catch (e: Exception) {
        println("try catch e=" + e)
    }

}
