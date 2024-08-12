package ru.marinovdev.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.marinovdev.data.tokens.dto.TokenDTO
import ru.marinovdev.domain.model.sign_in.SignInResponseRemote
import ru.marinovdev.domain.model.update_tokens.UserTokensDetails
import ru.marinovdev.domain.repository.TokensDataSourceRepository
import ru.marinovdev.features.auth_lackner.security.token.JwtTokenService
import ru.marinovdev.features.auth_lackner.security.token.TokenClaim
import ru.marinovdev.features.jwt_token.TokenConfig
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource

class UpdateTokensController(
    private val tokensDataSourceRepository: TokensDataSourceRepository,
    private val tokenService: JwtTokenService,
    private val tokenConfig: TokenConfig,
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            println(":::::::::::::::UpdateTokensController попытка обновить токен")
            val receive = call.receive<UserTokensDetails>()
            checkTokenVerification(
                accessToken = receive.accessToken,
                refreshToken = receive.refreshToken,
                userId = receive.userId,
                call = call
            )
        } catch (e: Exception) {
            println(":::::::::::::::try catch UpdateTokensController e=" + e)
        }
    }

    private fun checkTokenVerification(accessToken: String, refreshToken: String, userId: Int, call: ApplicationCall) {
        tokensDataSourceRepository.checkTokenVerification(
            token = accessToken,
            onVerifiedResult = { verified ->
                when (verified) {
                    StringResource.TOKEN_VERIFIED, StringResource.TOKEN_EXPIRED -> {
                        println(":::::::::::::::UpdateTokensController accessToken TOKEN_VERIFIED or TOKEN_EXPIRED")
                        checkRefreshTokenExists(refreshToken = refreshToken, userId = userId, call = call)
                    }

                    StringResource.TOKEN_NOT_VERIFIED -> {
                        println(":::::::::::::::checkTokenVerification accessToken Unauthorized")
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

    private fun checkRefreshTokenExists(refreshToken: String, userId: Int, call: ApplicationCall) {
        tokensDataSourceRepository.fetchRefreshTokenByUserId(
            userId = userId,
            onSuccess = { refreshTokenFromDb ->
                if (refreshTokenFromDb == null) {
                    println(":::::::::::::::checkRefreshTokenExists refreshToken Forbidden userId=" + userId)
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.NotFound.value,
                                message = StringResource.REFRESH_TOKEN_NOT_FOUND
                            )
                        )
                    }
                } else if (refreshTokenFromDb == refreshToken) {
                    tokensDataSourceRepository.checkTokenVerification(
                        token = refreshToken,
                        onVerifiedResult = { verified ->
                            when (verified) {
                                StringResource.TOKEN_VERIFIED -> {
                                    println(":::::::::::::::UpdateTokensController refreshToken TOKEN_VERIFIED or TOKEN_EXPIRED")
                                    generateRefreshTokenAndInsertToDb(
                                        userIdFromDb = userId,
                                        call = call,
                                        tokenConfig = tokenConfig
                                    )
                                }

                                StringResource.TOKEN_NOT_VERIFIED, StringResource.TOKEN_EXPIRED -> {
                                    println(":::::::::::::::checkTokenVerification refreshToken Unauthorized")
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
                } else {
                    println(":::::::::::::::checkRefreshTokenExists refreshToken Unauthorized")
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.Unauthorized.value,
                                message = StringResource.REFRESH_TOKEN_UNAUTHORIZED
                            )
                        )
                    }
                }
            },
            onFailure = { e ->
                println(":::::::::::UpdateTokensController checkRefreshTokenExists failure e" + e)
                runBlocking {
                    call.respond(
                        MessageResponse(
                            HttpStatusCode.InternalServerError.value,
                            StringResource.UPDATE_REFRESH_TOKEN_ERROR + e.localizedMessage
                        )
                    )
                }
            }
        )
    }

    private fun generateRefreshTokenAndInsertToDb(userIdFromDb: Int, call: ApplicationCall, tokenConfig: TokenConfig) {
        val refreshToken = tokenService.generateRefreshToken(
            refreshTokenConfig = tokenConfig.getRefreshTokenConfig(),
            TokenClaim( // - объект, содержащий имя и значение утверждения (claim), которое будет добавлено в токен.
                userId = "userId",
                userIdValue = userIdFromDb
            )
        )
        deleteAndInsertRefreshTokenByUserId(userId = userIdFromDb, refreshToken = refreshToken, call = call)
    }

    private fun deleteAndInsertRefreshTokenByUserId(userId: Int, refreshToken: String, call: ApplicationCall) {
        println("::::::::::::::::::UpdateTokensController deleteAndInsertRefreshTokenByUserId")
        tokensDataSourceRepository.deleteRefreshTokenByUserId(
            userId = userId,
            onSuccess = {
                tokensDataSourceRepository.insertRefreshToken(
                    TokenDTO(
                        userId = userId,
                        refreshToken = refreshToken
                    ),
                    onSuccess = {
                        println("::::::::::: onSuccess insertRefreshToken")
                        fetchRefreshTokenByUserId(userId = userId, call = call)
                    },
                    onFailure = { e ->
                        println("::::::::::: onFailure insertRefreshToken")
                        runBlocking {
                            call.respond(
                                MessageResponse(
                                    HttpStatusCode.InternalServerError.value,
                                    StringResource.INSERT_REFRESH_TOKEN_ERROR + e.localizedMessage
                                )
                            )
                        }
                    })
            },
            onFailure = {
                println(":::::::::::SignInController deleteRefreshTokenByUserId onFailure ")
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

    private fun fetchRefreshTokenByUserId(userId: Int, call: ApplicationCall) {
        tokensDataSourceRepository.fetchRefreshTokenByUserId(
            userId = userId,
            onSuccess = { refreshToken ->
                if (refreshToken != null) {
                    val accessToken = generateAccessToken(userIdFromDb = userId, tokenConfig = tokenConfig)
                    sendingTokensToClient(accessToken = accessToken, refreshToken = refreshToken, call = call)
                } else {
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                HttpStatusCode.InternalServerError.value,
                                StringResource.INSERT_REFRESH_TOKEN_ERROR
                            )
                        )
                    }
                }
            },
            onFailure = { e ->
                runBlocking {
                    call.respond(
                        MessageResponse(
                            HttpStatusCode.InternalServerError.value,
                            StringResource.INSERT_REFRESH_TOKEN_ERROR + e.localizedMessage
                        )
                    )
                }
            })
    }

    private fun generateAccessToken(userIdFromDb: Int, tokenConfig: TokenConfig): String {
        // тут мы уверены что юзер ввел правильный пароль
        // и генерируем  токен и прикрепить его к ответу чтобы пользователь мог сохранить его в настройках
        return tokenService.generateAccessToken(
            accessTokenConfig = tokenConfig.getAccessTokenConfig(), //объект конфигурации токена, содержащий настройки эмитента, аудитории, срока действия токена и секретный ключ для подписи токена.
            TokenClaim( // - объект, содержащий имя и значение утверждения (claim), которое будет добавлено в токен.
                userId = "userId",
                userIdValue = userIdFromDb
            )
        )
    }

    private fun sendingTokensToClient(accessToken: String, refreshToken: String, call: ApplicationCall) {
        runBlocking {
            val signInResponseRemoteJson = Json.encodeToString(
                SignInResponseRemote(
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            )
            println(":::::::::::getToken onSuccess")
            call.respond(
                MessageResponse(
                    HttpStatusCode.OK.value,
                    message = signInResponseRemoteJson
                )
            )
        }
    }
}