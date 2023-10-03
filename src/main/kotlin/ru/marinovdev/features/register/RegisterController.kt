package ru.marinovdev.features.register

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.marinovdev.database.tokens.TokenDTO
import ru.marinovdev.database.tokens.Tokens
import ru.marinovdev.database.users.UserDTO
import ru.marinovdev.database.users.Users
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource
import java.util.*

class RegisterController(private val call: ApplicationCall) {

    suspend fun registerNewUser() {
        try {
            val registerReceiveRemote = call.receive<RegisterReceiveRemote>() // получаем email от клиента

            Users.checkEmailExists(
                emailFromDb = registerReceiveRemote.email,
                onSuccess = { isEmailExists ->
                    if (isEmailExists) {
                        runBlocking {
                            println(":::::::::::registerNewUser onSuccess isEmailExists=" + isEmailExists + " уже существует")
                            call.respond(
                                MessageResponse(
                                    httpStatusCode = HttpStatusCode.Conflict.value,
                                    message = StringResource.USER_ALREADY_EXISTS
                                )
                            )
                        }
                    } else {
                        println(":::::::::::registerNewUser onSuccess isEmailExists=" + isEmailExists + " пока не существует")
                        insertUserIntoDatabase(registerReceiveRemote = registerReceiveRemote)
                    }
                },
                onFailure = {
                    println(":::::::::::registerNewUser onFailure isEmailExists")
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.InternalServerError.value,
                                message = StringResource.CHECK_EMAIL_EXISTS_ERROR
                            )
                        )
                    }
                }
            )
        } catch (e: Exception) {
            println(":::::::::::registerNewUser USER_REGISTRATION_ERROR")
            runBlocking {
                call.respond(
                    MessageResponse(
                        httpStatusCode = HttpStatusCode.BadRequest.value,
                        message = StringResource.USER_REGISTRATION_ERROR + e.localizedMessage
                    )
                )
            }
        }
    }

    private fun insertUserIntoDatabase(registerReceiveRemote: RegisterReceiveRemote) {
        Users.insertUser(
            userDTO = UserDTO(
                email = registerReceiveRemote.email,
                password = registerReceiveRemote.password
            ),
            onSuccess = {
                println(":::::::::::insertUserIntoDatabase onSuccess")
                insertTokenIntoDataBaseAndSend(registerReceiveRemote = registerReceiveRemote)
            },
            onFailure = { e ->
                println(":::::::::::insertUserIntoDatabase onFailure=" + e)
                runBlocking {
                    call.respond(
                        MessageResponse(
                            HttpStatusCode.InternalServerError.value,
                            StringResource.USER_CREATION_ERROR + e.localizedMessage
                        )
                    )
                }
            }
        )
    }

    private fun insertTokenIntoDataBaseAndSend(registerReceiveRemote: RegisterReceiveRemote) {

        val token = UUID.randomUUID().toString()
        Tokens.insertToken(
            tokenDTO = TokenDTO(
                email = registerReceiveRemote.email,
                token = token
            ),
            onSuccess = {
                runBlocking {
                    val registerResponseRemoteJson = Json.encodeToString(RegisterResponseRemote(token = token))
                    println(":::::::::::insertTokenIntoDataBaseAndSend onSuccess")
                    call.respond(
                        MessageResponse(
                            HttpStatusCode.OK.value,
                            message = registerResponseRemoteJson
                        )
                    )
                }
            },
            onFailure = { e ->
                println(":::::::::::insertTokenIntoDataBaseAndSend onFailure=" + e)
                runBlocking {
                    call.respond(
                        MessageResponse(
                            HttpStatusCode.InternalServerError.value,
                            StringResource.TOKEN_CREATION_ERROR + e.localizedMessage
                        )
                    )
                }
            }
        )
    }
}