package ru.marinovdev.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.domain.model.forgot_password.UserPasswordRemote
import ru.marinovdev.domain.repository.UsersDataSourceRepository
import ru.marinovdev.features.auth_lackner.security.hashing_password.HashingService
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource

class UserPasswordController(
    private val hashingService: HashingService,
    private val usersDataSourceRepository: UsersDataSourceRepository,
) {
    suspend fun changePassword(call: ApplicationCall, hoconApplicationConfig: HoconApplicationConfig) {
        try {
            val receive = call.receive<UserPasswordRemote>()

            val passwordHex: String = hashingService.generatePasswordHex(
                password = receive.password,
                hoconApplicationConfig = hoconApplicationConfig
            )
            usersDataSourceRepository.updatePasswordHex(
                emailReceived = receive.email,
                passwordGenerated = passwordHex,
                onSuccess = {
                    println(":::::::::::changePassword onSuccess")
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.OK.value,
                                message = StringResource.USER_HAS_SUCCESSFULLY_REGISTERED
                            )
                        )
                    }
                },
                onFailure = {
                    println(":::::::::::changePassword onFailure")
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.InternalServerError.value,
                                message = StringResource.USER_REGISTRATION_ERROR
                            )
                        )
                    }
                }
            )
        } catch (e: Exception) {
            println(":::::::::::changePassword try catch e=" + e.localizedMessage)
            runBlocking {
                call.respond(
                    MessageResponse(
                        httpStatusCode = HttpStatusCode.BadRequest.value,
                        message = e.localizedMessage
                    )
                )
            }
        }
    }
}