package ru.marinovdev.features.register

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.marinovdev.database.tokens.TokenDTO
import ru.marinovdev.database.tokens.Tokens
import ru.marinovdev.database.users.UserDTO
import ru.marinovdev.database.users.Users
import ru.marinovdev.utils.isValidEmail
import java.util.*

class RegisterController(private val call: ApplicationCall) {

    // передаем модель которую получили от клиента
    suspend fun registerNewUser() {
        val registerReceiveRemote = call.receive<RegisterReceiveRemote>() // получаем email от клиента
        if (!registerReceiveRemote.email.isValidEmail()) {
            call.respond(HttpStatusCode.BadRequest, "email is not valid")
        }

        // убедимся что такой юзер вообще такой есть
        val userDTO = Users.fetch(registerReceiveRemote.login)

        if (userDTO != null) {
            call.respond(HttpStatusCode.Conflict, "user already exists")
        } else {
            val token = UUID.randomUUID().toString()
            var id = UUID.randomUUID().toString()

            // НАПИСАТЬ ПРОВЕРКУ СОВПАДАЕТ ЛИ НОВЫЙ СГЕНЕРЕННЫЙ ТОКЕН ИЗ БД
            // Проверяем, существует ли уже пользователь с таким rowId
//            while (Tokens.isUserExist(id)) {
//                id = UUID.randomUUID().toString() // Генерируем новый rowId
//            }


            Users.insert(
                userDTO = UserDTO(
                    login = registerReceiveRemote.login,
                    password = registerReceiveRemote.password,
                    username = "", // на старте не заполнен (почему?)
                    email = registerReceiveRemote.email
                )
            )

            Tokens.insert(
                tokenDTO = TokenDTO(
                    rowId = UUID.randomUUID().toString(),
                    login = registerReceiveRemote.login,
                    token = token
                )
            )

            call.respond(RegisterResponseRemote(token = token))
        }
    }
}