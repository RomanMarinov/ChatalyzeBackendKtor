package ru.marinovdev.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.domain.model.delete_profile.DeleteProfileReceiveRemote
import ru.marinovdev.domain.repository.TokensDataSourceRepository
import ru.marinovdev.domain.repository.UsersDataSourceRepository
import ru.marinovdev.features.auth_lackner.security.token.JwtTokenService
import ru.marinovdev.features.auth_lackner.security.token.TokenPayload
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource

class DeleteProfileController(
    private val jwtTokenService: JwtTokenService,
    private val usersDataSourceRepository: UsersDataSourceRepository,
    private val tokensDataSourceRepository: TokensDataSourceRepository
) {
    suspend fun delete(call: ApplicationCall) {
        val receive = call.receive<DeleteProfileReceiveRemote>()

        val tokenPayload: TokenPayload = jwtTokenService.decodeToken(token = receive.refreshToken)

        val userId = tokenPayload.userId
        val expiresIn = tokenPayload.expiresIn

        userId?.let {
            usersDataSourceRepository.fetchUserByUserId(
                id = it,
                onSuccess = { userDTO ->
                    if (userDTO != null) {
                        deleteUserByUserId(userId = it, call = call)
                    } else {
                        runBlocking {
                            println(":::::::::::SenderEmailController fetchUser onSuccess")
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
                                httpStatusCode = HttpStatusCode.InternalServerError.value,
                                message = StringResource.GET_USER_ERROR + e.localizedMessage
                            )
                        )
                    }
                }
            )
        } ?: run {
            runBlocking {
                call.respond(
                    MessageResponse(
                        httpStatusCode = HttpStatusCode.NotFound.value,
                        message = StringResource.USER_ID_NOT_FOUND
                    )
                )
            }
        }
    }

    private fun deleteUserByUserId(userId: Int, call: ApplicationCall) {
        usersDataSourceRepository.deleteUserByUserId(
            id = userId,
            onSuccess = {
                println(":::::::::::DeleteProfileController deleteUserByUserId onSuccess ")
                fetchRefreshTokenByUserId(userId = userId, call = call)
            },
            onFailure = { e ->
                runBlocking {
                    println(":::::::::::DeleteProfileController deleteUserByUserId onFailure ")
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.InternalServerError.value,
                            message = StringResource.DELETE_USER_ERROR + e.localizedMessage
                        )
                    )
                }
            }
        )
    }

    private fun fetchRefreshTokenByUserId(userId: Int, call: ApplicationCall) {
        tokensDataSourceRepository.fetchRefreshTokenByUserId(
            userId = userId,
            onSuccess = {
                deleteRefreshTokenByUserId(userId = userId, call = call)
            },
            onFailure = { e ->
                runBlocking {
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.InternalServerError.value,
                            message = StringResource.GET_TOKEN_ERROR + e.localizedMessage
                        )
                    )
                }
            }
        )
    }

    private fun deleteRefreshTokenByUserId(userId: Int, call: ApplicationCall) {
        tokensDataSourceRepository.deleteRefreshTokenByUserId(
            userId = userId,
            onSuccess = {
                runBlocking {
                    println(":::::::::::DeleteProfileController deleteRefreshTokenByUserId onSuccess ")
                    call.respond( // что будет правильным отправить клиенту
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.OK.value,
                            message = "Token successfully deleted"
                        )
                    )
                    return@runBlocking
                }
            },
            onFailure = {
                println(":::::::::::DeleteProfileController deleteRefreshTokenByUserId onFailure ")
                runBlocking {
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.InternalServerError.value,
                            message = StringResource.DELETE_REFRESH_TOKEN_ERROR
                        )
                    )
                }
            }
        )
    }
}