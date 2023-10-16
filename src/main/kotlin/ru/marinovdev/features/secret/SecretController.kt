//package ru.marinovdev.features.secret
//
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.auth.*
//import io.ktor.server.auth.jwt.*
//import io.ktor.server.response.*
//import kotlinx.coroutines.runBlocking
//
//class SecretController(private val call: ApplicationCall) {
//    fun getSecretInfo() {
//        val principal = call.principal<JWTPrincipal>()
//        val emailId = principal?.getClaim("emailId", String::class)
//        runBlocking {
//            call.respond(HttpStatusCode.OK, "Your email $emailId")
//            return@runBlocking
//        }
//    }
//}