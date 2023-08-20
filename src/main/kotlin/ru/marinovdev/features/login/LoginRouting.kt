package ru.marinovdev.features.login

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.marinovdev.cache.InMemoryCache
import ru.marinovdev.cache.TokenCache
import ru.marinovdev.plugins.Test
import java.util.UUID

fun Application.configureLoginRouting() {
    routing {
        post("/login") {
            val receive = call.receive<LoginReceiveRemote>() // получаем логин от клиента

            val firstPassword = InMemoryCache.userList.firstOrNull { it.password == receive.password }

//            val login = InMemoryCache.userList.firstOrNull() { it.login == receive.login }
//            if () {
//
//            }



            if (firstPassword == null) {
                call.respond(HttpStatusCode.BadRequest, "user not found")
            } else {
                if (firstPassword.password == receive.password) {
                    val token = UUID.randomUUID().toString()
                    InMemoryCache.token.add(TokenCache(login = receive.login, token = token))
                    call.respond(LoginResponseRemote(token = token))
                } else {
                    // отдаем клиенту ответ 400 некоретные данные
                    call.respond(HttpStatusCode.BadRequest, "invalid password")
                }
            }
        }
    }
}