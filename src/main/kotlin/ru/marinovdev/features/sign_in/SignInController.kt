package ru.marinovdev.features.sign_in

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.marinovdev.database.tokens.Tokens
import ru.marinovdev.database.users.Users
import ru.marinovdev.features.register.RegisterResponseRemote
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource


class SignInController(private val call: ApplicationCall) {

    suspend fun performSignIn() {
        val receive = call.receive<SignInReceiveRemote>() // получаем логин от клиента
        println("SignInController receive=" + receive)

        // получю от клиента майл и поль

        // проверяю


        Users.fetchUser(
            receivedEmail = receive.email,
            onSuccess = {
                if (receive.email == it?.email && receive.password == it.password) {
                    fetchToken(email = it.email)
                } else {
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.BadRequest.value,
                                message = "User not found"
                            )
                        )
                    }
                }
            },
            onFailure = { e ->
                runBlocking {
                    call.respond(
                        MessageResponse(
                            HttpStatusCode.InternalServerError.value,
                            StringResource.USER_SIGN_IN_ERROR + e.localizedMessage
                        )
                    )
                }
            }
        )
    }

    private fun fetchToken(email: String) {
        Tokens.fetchToken(
            receivedEmail = email,
            onSuccess = {
                runBlocking {
                    val registerResponseRemoteJson = Json.encodeToString(RegisterResponseRemote(token = it))
                    println(":::::::::::getToken onSuccess")
                    call.respond(
                        MessageResponse(
                            HttpStatusCode.OK.value,
                            message = registerResponseRemoteJson
                        )
                    )
                }
            },
            onFailure = { e ->
                runBlocking {
                    call.respond(
                        MessageResponse(
                            HttpStatusCode.InternalServerError.value,
                            StringResource.GET_TOKEN_ERROR + e.localizedMessage
                        )
                    )
                }
            }
        )
    }
}