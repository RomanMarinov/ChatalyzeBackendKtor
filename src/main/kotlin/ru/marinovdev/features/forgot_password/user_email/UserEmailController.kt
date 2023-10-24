package ru.marinovdev.features.forgot_password.user_email

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.database.codes.CodeDTO
import ru.marinovdev.database.codes.Codes
import ru.marinovdev.database.users.Users
import ru.marinovdev.features.auth_lackner.security.hashing_code.HashingCodeService
import ru.marinovdev.features.auth_lackner.security.hashing_code.SaltedHashCode
import ru.marinovdev.features.send_email.SendEmailToTheUser
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource
import java.time.Instant
import kotlin.random.Random


class UserEmailController(
    private val call: ApplicationCall,
    private val hashingCodeService: HashingCodeService
) {
    suspend fun fetchAndSend() {

        try {
            val receivedEmail = call.receive<UserEmailRemote>() // type data class
            println(":::::::::::Пришло от клиента при отправке от него почты если он забыл пароль =${receivedEmail.email}")
            findUserIdByEmail(email = receivedEmail.email)

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

    private fun findUserIdByEmail(email: String) {
        Users.findUserIdByEmail(
            emailFromDb = email,
            onSuccess = { userId ->
                if (userId != null) {
                    println(":::::::::::findUserIdByEmail onSuccess user != null")
                    checkCodeToDb(userId = userId, email = email)
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

    private fun checkCodeToDb(userId: Int, email: String) {
        Codes.deleteCode(
            receiveUserId = userId,
            onSuccess = {
                println(":::::::::::checkCodeToDb onSuccess")
                insertCodeToDb(userId = userId, email = email)
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

    private fun insertCodeToDb(userId: Int, email: String) {

        val code = generateCode()

        val saltedHashCode: SaltedHashCode = hashingCodeService.generateSaltHashCode(code = code)

        val timeOfCreation = getCurrentTimestamp()
        val codeDTO = CodeDTO(
            userId = userId,
            timeOfCreation = timeOfCreation,
            hashCodeSalt = saltedHashCode.hashCodeSalt,
            salt = saltedHashCode.salt
        )
        println(":::::::::::codeDTO=" + codeDTO)
// :codeDTO=CodeDTO(
// userId=3,
// timeOfCreation=1697573106004,
// hashCodeSalt=caeafe8a6e4f3491679076330713584fe18c7f382c9e91d1e20684232e3b311e,
// salt=7b855b15681c9f0214f6cf8990b0764afd5ede613482c1d6481c4f1943a2f217)
        Codes.insertCodeToDb(
            codeDTO,
            onSuccess = {
                println(":::::::::::insertCodeToDb onSuccess")

//                runBlocking {
//                    println(":::::::::::sendPasswordByEmail sendEmail onSuccess")
//                    call.respond(
//                        MessageResponse(
//                            httpStatusCode = HttpStatusCode.OK.value,
//                            message = "The letter was successfully sent to ! " +
//                                    "\nWe also recommend checking your SPAM address"
//                        )
//                    )
//                }

                 sendCodeByEmail(code = code, email = email)
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

    private fun sendCodeByEmail(code: String, email: String) {
        //val email = "marinov37@mail.ru"
        //val password = "123qweRT"

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