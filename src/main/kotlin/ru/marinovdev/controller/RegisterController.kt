package ru.marinovdev.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.data.users.dto.UserDTO
import ru.marinovdev.domain.model.register.RegisterReceiveRemote
import ru.marinovdev.domain.repository.UsersDataSourceRepository
import ru.marinovdev.features.auth_lackner.security.hashing_password.HashingService
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource

class RegisterController(
    private val hashingService: HashingService,
    private val usersDataSourceRepository: UsersDataSourceRepository
) {
    suspend fun registerUser(call: ApplicationCall, hoconApplicationConfig: HoconApplicationConfig) {
        try {
            val registerReceiveRemote = call.receive<RegisterReceiveRemote>() // получаем email от клиента
            usersDataSourceRepository.checkEmailExists(
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
                            return@runBlocking
                        }
                    } else {
                        println(":::::::::::registerNewUser onSuccess isEmailExists=" + isEmailExists + " пока не существует")
                        insertUserIntoDatabase(
                            registerReceiveRemote = registerReceiveRemote,
                            hashingService = hashingService,
                            call = call,
                            hoconApplicationConfig = hoconApplicationConfig
                        )
                    }
                },
                onFailure = { e ->
                    println(":::::::::::registerNewUser onFailure isEmailExists e=" + e.localizedMessage)
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.InternalServerError.value,
                                message = StringResource.CHECK_EMAIL_EXISTS_ERROR + e.localizedMessage
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

    private fun insertUserIntoDatabase(
        registerReceiveRemote: RegisterReceiveRemote,
        hashingService: HashingService,
        call: ApplicationCall,
        hoconApplicationConfig: HoconApplicationConfig
    ) {

        val passwordHex: String = hashingService.generatePasswordHex(
            password = registerReceiveRemote.password,
            hoconApplicationConfig = hoconApplicationConfig
        )

        usersDataSourceRepository.insertUser(
            userDTO = UserDTO(
                email = registerReceiveRemote.email,
                password = passwordHex,
                //salt = saltHash.salt,
            ),
            onSuccess = {
                println(":::::::::::insertUserIntoDatabase onSuccess")
                runBlocking {
                    call.respond(
                        MessageResponse(
                            HttpStatusCode.OK.value,
                            StringResource.USER_HAS_SUCCESSFULLY_REGISTERED
                        )
                    )
                    return@runBlocking
                }
                return@insertUser
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
}