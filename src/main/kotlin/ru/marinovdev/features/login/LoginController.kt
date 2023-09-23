package ru.marinovdev.features.login

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.marinovdev.database.tokens.TokenDTO
import ru.marinovdev.database.tokens.Tokens
import ru.marinovdev.database.users.Users
import java.util.*

class LoginController (private val call: ApplicationCall) {

    // при выполнении входа
    suspend fun performLogin() {
        val receive = call.receive<LoginReceiveRemote>() // получаем логин от клиента

        val userDTO = Users.fetch(receive.login) // получаем из бд ответ по такому логину
//            val login = InMemoryCache.userList.firstOrNull() { it.login == receive.login }
//            if () {
//
//            }

        if (userDTO == null) {
            call.respond(HttpStatusCode.BadRequest, "user not found")
        } else {
            if (userDTO.password == receive.password) {
                val token = UUID.randomUUID().toString()
                Tokens.insert(
                    tokenDTO = TokenDTO(
                        rowId = UUID.randomUUID().toString(),
                        login = receive.login,
                        token = token
                    )
                )

                call.respond(LoginResponseRemote(token = token))
            } else {
                // отдаем клиенту ответ 400 некоретные данные
                call.respond(HttpStatusCode.BadRequest, "invalid password")
            }
        }
    }
}