//package ru.marinovdev.features.authenticate
//
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.response.*
//import kotlinx.coroutines.runBlocking
//import ru.marinovdev.model.MessageResponse
//import ru.marinovdev.utils.StringResource
//
//class AuthenticateController(private val call: ApplicationCall) {
//     fun executeAuthenticate() {
//        runBlocking {
//            call.respond(
//                MessageResponse(
//                    HttpStatusCode.OK.value,
//                    StringResource.USER_SUCCESSFULLY_AUTHENTICATE
//                )
//            )
//            return@runBlocking
//        }
//    }
//}