package ru.marinovdev.features.send_email

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.database.users.UserDTO
import ru.marinovdev.database.users.Users
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource

class SenderEmailController(private val call: ApplicationCall) {

    // передаем модель которую получили от клиента
    suspend fun fetchAndSend() {
        try {
            val receivedEmail = call.receive<SenderEmailReceiveRemote>()
            println(":::::::::::Пришло от клиента при отправке от него почты если он забыл пароль =$receivedEmail")

            Users.fetchUser(
                receivedEmail = receivedEmail.email,
                onSuccess = {
                    if (it == null) {
                        runBlocking {
                            call.respond(
                                MessageResponse(
                                    httpStatusCode = HttpStatusCode.BadRequest.value,
                                    message = "User not found"
                                )
                            )
                        }
                    } else {
                        sendPasswordByEmail(userDTO = it)
                    }
                },
                onFailure = { e ->
                    println(":::::::::::fetchUser onFailure=$e")
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.InternalServerError.value,
                                message = StringResource.GET_USER_ERROR + e.localizedMessage
                            )
                        )
                    }
                }
            )
        } catch (e: Exception) {
            println(":::::::::::try catch fetchAndSend e=$e")
            runBlocking {
                call.respond(
                    MessageResponse(
                        httpStatusCode = HttpStatusCode.BadRequest.value,
                        message = StringResource.FETCH_AND_SEND_ERROR + e.localizedMessage
                    )
                )
            }
        }
    }

    private fun sendPasswordByEmail(userDTO: UserDTO) {
        val email = "marinov37@mail.ru"
        val password = "123qweRT"

        SendEmailToTheUser.sendEmail(
            emailForSending = email,
            password = password,
            onSuccess = { // 200
                runBlocking {
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.OK.value,
                            message = "The letter was successfully sent to $email! " +
                                    "\nWe also recommend checking your SPAM address"
                        )
                    )
                }
            },
            onFailure = { e -> // 500
                runBlocking {
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.InternalServerError.value,
                            message = "An error occurred on the server when sending a letter: ${e.message}"
                        )
                    )
                }
            }
        )
    }
}