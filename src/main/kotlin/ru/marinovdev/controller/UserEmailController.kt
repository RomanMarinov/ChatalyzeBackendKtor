package ru.marinovdev.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.data.code.CodeDTO
import ru.marinovdev.domain.model.forgot_password.UserEmailRemote
import ru.marinovdev.domain.repository.CodeDataSourceRepository
import ru.marinovdev.domain.repository.UsersDataSourceRepository
import ru.marinovdev.features.auth_lackner.security.hashing_code.HashingCodeService
import ru.marinovdev.features.auth_lackner.security.hashing_code.SaltedHashCode
import ru.marinovdev.features.send_email.SendEmailToTheUser
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource
import java.time.Instant
import kotlin.random.Random

class UserEmailController(
    private val hashingCodeService: HashingCodeService,
    private val usersDataSourceRepository: UsersDataSourceRepository,
    private val codeDataSourceRepository: CodeDataSourceRepository
) {
    suspend fun fetchAndSend(call: ApplicationCall) {
        try {
            val receivedEmail = call.receive<UserEmailRemote>() // type data class
            findUserIdByEmail(email = receivedEmail.email, call = call)
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

    private fun findUserIdByEmail(email: String, call: ApplicationCall) {
        usersDataSourceRepository.findUserIdByEmail(
            emailFromDb = email,
            onSuccess = { userId ->
                if (userId != null) {
                    println(":::::::::::findUserIdByEmail onSuccess user != null")
                    checkCodeToDb(userId = userId, email = email, call = call)
                } else {
                    println(":::::::::::findUserIdByEmail onSuccess user = null")
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.NotFound.value,
                                message = "User id not found"
                            )
                        )
                        return@runBlocking
                    }
                }
            },
            onFailure = { e ->
                println(":::::::::::findUserIdByEmail onFailure")
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

    private fun checkCodeToDb(userId: Int, email: String, call: ApplicationCall) {
        codeDataSourceRepository.deleteCode(
            receiveUserId = userId,
            onSuccess = {
                println(":::::::::::checkCodeToDb onSuccess")
                insertCodeToDb(userId = userId, email = email, call = call)
            },
            onFailure = { e ->
                println(":::::::::::checkCodeToDb onFailure e=" + e.localizedMessage)
                runBlocking {
                    call.respond(
                        MessageResponse(
                            HttpStatusCode.InternalServerError.value,
                            StringResource.DELETE_CODE_ERROR + e.localizedMessage
                        )
                    )
                }
            }
        )
    }

    private fun insertCodeToDb(userId: Int, email: String, call: ApplicationCall) {

        val code = generateCode()

        val saltedHashCode: SaltedHashCode = hashingCodeService.generateSaltHashCode(code = code)

        val timeOfCreation = getCurrentTimestamp()
        val codeDTO = CodeDTO(
            userId = userId,
            timeOfCreation = timeOfCreation,
            hashCodeSalt = saltedHashCode.hashCodeSalt,
            salt = saltedHashCode.salt
        )
        codeDataSourceRepository.insertCodeToDb(
            codeDTO,
            onSuccess = {
                println(":::::::::::insertCodeToDb onSuccess")
                 sendCodeByEmail(code = code, email = email, call = call)
            },
            onFailure = { e ->
                println(":::::::::::insertCodeToDb onFailure")
                runBlocking {
                    call.respond(
                        MessageResponse(
                            HttpStatusCode.InternalServerError.value,
                            StringResource.INSERT_CODE_ERROR + e.localizedMessage
                        )
                    )
                }
            }
        )
    }

    private fun sendCodeByEmail(code: String, email: String, call: ApplicationCall) {
        SendEmailToTheUser.sendEmail(
            emailForSending = email,
            code = code,
            onSuccess = { // 200
                runBlocking {
                    println(":::::::::::sendPasswordByEmail sendEmail onSuccess")
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.OK.value,
                            message = "The letter was successfully sent to ${email}! " +
                                    "\nWe also recommend checking your SPAM address"
                        )
                    )
                }
            },
            onFailure = { e -> // 500
                runBlocking {
                    println(":::::::::::sendPasswordByEmail sendEmail onFailure")
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

    private fun getCurrentTimestamp(): Long {
        return Instant.now().toEpochMilli()
    }

    private fun generateCode(): String {
        var code = ""
        repeat(5) {
            val number = Random.nextInt(0, 9)
            code += number
        }
        return code
    }
}