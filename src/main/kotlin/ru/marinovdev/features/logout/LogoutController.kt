package ru.marinovdev.features.logout

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.database.tokens.Tokens
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource

class LogoutController(
    private val call: ApplicationCall
) {
    suspend fun logoutUser() {
        val receive = call.receive<LogoutReceiveRemote>()
        println(":::::::::::logoutUser receive=" + receive)
        Tokens.checkRefreshTokenToDb(
            refreshToken = receive.refresh_token,
            onSuccess = { refreshToken ->

                if (refreshToken != null) {
                    println(":::::::::::logoutUser onSuccess не null checkRefreshTokenToDb=" + refreshToken)
                    deleteRefreshTokenToDb(refreshToken = refreshToken)
                } else {
                    println(":::::::::::logoutUser onSuccess null checkRefreshTokenToDb=" + refreshToken)
                }
            },
            onFailure = { e ->
                println(":::::::::::logoutUser onFailure checkRefreshTokenToDb")
                runBlocking {
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.InternalServerError.value,
                            message = StringResource.CHECK_REFRESH_TOKEN_ERROR
                        )
                    )
                }
            })
    }

    private fun deleteRefreshTokenToDb(refreshToken: String) {
        Tokens.deleteRefreshTokenToDb(
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
            })
    }
}