package ru.marinovdev.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.data.user_manager.UserSocketManager
import ru.marinovdev.domain.model.logout.LogoutReceiveRemote
import ru.marinovdev.domain.repository.TokensDataSourceRepository
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource

class LogoutController(private val tokensDataSourceRepository: TokensDataSourceRepository) {

    suspend fun logoutUser(call: ApplicationCall) {
        val receive = call.receive<LogoutReceiveRemote>()
        verifyRefreshToken(receive = receive, call = call)
    }

    private fun verifyRefreshToken(receive: LogoutReceiveRemote, call: ApplicationCall) {
        tokensDataSourceRepository.checkTokenVerification(
            token = receive.refresh_token,
            onVerifiedResult = { verified ->
                when(verified) {
                    StringResource.TOKEN_VERIFIED -> {
                        println(":::::::::::::::LogoutController verifyRefreshToken TOKEN_VERIFIED")
                        checkRefreshTokenToDb(refreshToken = receive.refresh_token, senderPhone = receive.sender_phone, call = call)
                    }
                    StringResource.TOKEN_NOT_VERIFIED, StringResource.TOKEN_EXPIRED -> {
                        println(":::::::::::::::LogoutController verifyRefreshToken TOKEN_NOT_VERIFIED or TOKEN_EXPIRED")
                        runBlocking {
                            call.respond(
                                MessageResponse(
                                    httpStatusCode = HttpStatusCode.Unauthorized.value,
                                    message = StringResource.ACCESS_TOKEN_UNAUTHORIZED
                                )
                            )
                        }
                    }
                }
            }
        )
    }

    private fun checkRefreshTokenToDb(refreshToken: String, senderPhone: String, call: ApplicationCall) {
        tokensDataSourceRepository.checkRefreshTokenToDb(
            refreshToken = refreshToken,
            onSuccess = { refToken ->
                if (refToken != null) {
                    println(":::::::::::logoutUser onSuccess не null checkRefreshTokenToDb=" + refToken)
                    deleteRefreshTokenToDb(refreshToken = refToken, senderPhone = senderPhone, call = call)
                } else {
                    println(":::::::::::logoutUser onSuccess null checkRefreshTokenToDb=" + refToken)
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.NotFound.value,
                                message = StringResource.THIS_REFRESH_NOT_FOUND
                            )
                        )
                    }
                }
            },
            onFailure = { e ->
                println(":::::::::::logoutUser onFailure checkRefreshTokenToDb")
                runBlocking {
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.InternalServerError.value,
                            message = StringResource.CHECK_REFRESH_TOKEN_ERROR + e.localizedMessage
                        )
                    )
                }
            }
        )
    }

    private fun deleteRefreshTokenToDb(refreshToken: String, senderPhone: String, call: ApplicationCall) {
        tokensDataSourceRepository.deleteRefreshTokenToDb(
            refreshToken = refreshToken,
            onSuccess = {
                runBlocking {
                    println(":::::::::::logoutUser onSuccess deleteRefreshTokenToDb")
                    call.respond( // что будет правильным отправить клиенту
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.OK.value,
                            message = "Token successfully deleted"
                        )
                    )
                    removeMemberByUserPhone(userPhone = senderPhone)

                    return@runBlocking
                }
            },
            onFailure = {
                println(":::::::::::logoutUser onFailure deleteRefreshTokenToDb")
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

    private fun removeMemberByUserPhone(userPhone: String) {
        if (UserSocketManager.checkContainsUser(userPhone = userPhone)) {
            println("::::::::::::::::::::LogoutController removeMemberByUserPhone")
            UserSocketManager.removeMemberByUserPhone(userPhone = userPhone)
        }
    }
}