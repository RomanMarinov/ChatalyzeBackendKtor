package ru.marinovdev.features.forgot_password.user_password

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.database.users.Users
import ru.marinovdev.features.auth_lackner.security.hashing_password.SHA256HashingService
import ru.marinovdev.features.auth_lackner.security.hashing_password.SaltedHash
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource

class UserPasswordController(
    private val call: ApplicationCall,
    private val hashingService: SHA256HashingService
) {

    suspend fun changePassword() {
        try {
            val receive = call.receive<UserPasswordRemote>()

            val saltHash: SaltedHash = hashingService.generateSaltHash(password = receive.password)
            Users.updatePasswordAndSalt(
                emailReceived = receive.email,
                passwordGenerated = saltHash.hashPasswordSalt,
                saltGenerated = saltHash.salt,
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