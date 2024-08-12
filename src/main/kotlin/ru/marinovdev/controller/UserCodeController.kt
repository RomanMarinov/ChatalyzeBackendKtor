package ru.marinovdev.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.data.code.CodeDTO
import ru.marinovdev.domain.model.forgot_password.UserCodeRemote
import ru.marinovdev.domain.repository.CodeDataSourceRepository
import ru.marinovdev.domain.repository.UsersDataSourceRepository
import ru.marinovdev.features.auth_lackner.security.hashing_code.HashingCodeService
import ru.marinovdev.features.auth_lackner.security.hashing_code.SaltedHashCode
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource
import java.time.Duration
import java.time.Instant
import java.util.*

class UserCodeController(
    private val hashingCodeService: HashingCodeService,
    private val usersDataSourceRepository: UsersDataSourceRepository,
    private val codeDataSourceRepository: CodeDataSourceRepository
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            val receive = call.receive<UserCodeRemote>()

            usersDataSourceRepository.findUserIdByEmail(
                emailFromDb = receive.email,
                onSuccess = { userId ->
                    userId?.let {
                        fetchCode(userId = userId, userCodeRemote = receive, call = call)
                    } ?: run {
                        runBlocking {
                            call.respond(MessageResponse(
                                httpStatusCode = HttpStatusCode.NotFound.value,
                                message = StringResource.USER_ID_NOT_FOUND
                            ))
                        }
                    }
                },
                onFailure = {
                    runBlocking {
                        call.respond(MessageResponse(
                            httpStatusCode = HttpStatusCode.InternalServerError.value,
                            message = StringResource.FETCH_USER_ID_BY_EMAIL_ERROR
                        ))
                    }
                }
            )
        } catch (e: Exception) {
            runBlocking {
                call.respond(MessageResponse(
                    httpStatusCode = HttpStatusCode.BadRequest.value,
                    message = StringResource.FETCH_USER_ID_BY_EMAIL_ERROR + e.localizedMessage
                ))
            }
        }
    }

    private fun fetchCode(userId: Int, userCodeRemote: UserCodeRemote, call: ApplicationCall) {
        codeDataSourceRepository.fetchCode(
            receiveUserId = userId,
            onSuccess = { codeDTO ->
                checkCode(codeDTO = codeDTO, userCodeRemote = userCodeRemote, userId = userId, call = call)
            },
            onFailure = {
                runBlocking {
                    call.respond(MessageResponse(
                        httpStatusCode = HttpStatusCode.InternalServerError.value,
                        message = StringResource.FETCH_CODE_ERROR
                    ))
                }
            }
        )
    }

    private fun checkCode(codeDTO: CodeDTO, userCodeRemote: UserCodeRemote, userId: Int, call: ApplicationCall) {

        val saltedHashCode = SaltedHashCode(
            hashCodeSalt = codeDTO.hashCodeSalt,
            salt = codeDTO.salt
        )
        val resultCode = hashingCodeService.verifyCode(code = userCodeRemote.code.toString(), saltedHash = saltedHashCode)
        if (resultCode) {
            val currentTime = Date().time

            val lifeTimeCodeDefault = 60000
            val differenceInMillis = getDifferenceInMillis(
                timeOfCreation = codeDTO.timeOfCreation,
                currentTime = currentTime
            )

            if (differenceInMillis <= lifeTimeCodeDefault) {
                runBlocking {
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.OK.value,
                            message = StringResource.CORRECT_CODE
                        )
                    )
                }
            } else {
                runBlocking {
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.Gone.value,
                            message = StringResource.TIME_IS_UP
                        )
                    )
                }
            }
            deleteCodeByUserIdToDb(userId = userId)
        } else {
            runBlocking {
                call.respond(
                    MessageResponse(
                        httpStatusCode = HttpStatusCode.OK.value,
                        message = StringResource.INCORRECT_CODE
                    )
                )
            }
        }
    }

    private fun getDifferenceInMillis(timeOfCreation: Long, currentTime: Long): Long {
        val instant1 = Instant.ofEpochMilli(timeOfCreation)
        val instant2 = Instant.ofEpochMilli(currentTime)
        val duration = Duration.between(instant1, instant2)
        return duration.toMillis()
    }

    private fun deleteCodeByUserIdToDb(userId: Int) {
        codeDataSourceRepository.deleteCode(
            receiveUserId = userId,
            onSuccess = {
                println(":::::::::::deleteCodeByUserIdToDb onSuccess")
            },
            onFailure = { e ->
                println(":::::::::::try catch deleteCodeByUserIdToDb e=$e")
            }
        )
    }
}