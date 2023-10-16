package ru.marinovdev.features.delete_profile

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.database.tokens.Tokens
import ru.marinovdev.database.users.Users
import ru.marinovdev.features.auth_lackner.security.token.JwtTokenService
import ru.marinovdev.features.auth_lackner.security.token.TokenPayload
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource

class DeleteProfileController(
    private val call: ApplicationCall,
    private val jwtTokenService: JwtTokenService
) {
    suspend fun delete() {
        val receive = call.receive<DeleteProfileReceiveRemote>()
        println("::::::::::::::::::DeleteProfileController delete receive refreshToken=" + receive.refreshToken)

        val tokenPayload: TokenPayload = jwtTokenService.decodeRefreshToken(receive.refreshToken)

        val userId = tokenPayload.userId
        val expiresIn = tokenPayload.expiresIn

        println("::::::::::::::::::DeleteProfileController userId=" + userId)
        println("::::::::::::::::::DeleteProfileController expiresIn=" + expiresIn)

        // по userId удалить строку из users и строку token

        userId?.let {
            Users.fetchUserByUserId(
                id = it,
                onSuccess = { userDTO ->
                    if (userDTO != null) {
                        deleteUserByUserId(userId = it)
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
        }
    }

    private fun deleteUserByUserId(userId: Int) {
        Users.deleteUserByUserId(
            id = userId,
            onSuccess = {
                println(":::::::::::DeleteProfileController deleteUserByUserId onSuccess ")
                fetchRefreshTokenByUserId(userId = userId)
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

    private fun fetchRefreshTokenByUserId(userId: Int) {
        Tokens.fetchRefreshTokenByUserId(
            userId = userId,
            onSuccess = {
                deleteRefreshTokenByUserId(userId = userId)
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

    private fun deleteRefreshTokenByUserId(userId: Int) {
        Tokens.deleteRefreshTokenByUserId(
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
            })
    }
}