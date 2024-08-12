package ru.marinovdev.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.marinovdev.data.tokens.dto.TokenDTO
import ru.marinovdev.data.users.dto.UserDTO
import ru.marinovdev.domain.model.sign_in.SignInReceiveRemote
import ru.marinovdev.domain.model.sign_in.SignInResponseRemote
import ru.marinovdev.domain.repository.TokensDataSourceRepository
import ru.marinovdev.domain.repository.UsersDataSourceRepository
import ru.marinovdev.features.auth_lackner.security.hashing_password.HashingService
import ru.marinovdev.features.auth_lackner.security.token.JwtTokenService
import ru.marinovdev.features.auth_lackner.security.token.TokenClaim
import ru.marinovdev.features.jwt_token.TokenConfig
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource

class SignInController(
    private val hashingService: HashingService,
    private val tokenService: JwtTokenService,
    private val tokenConfig: TokenConfig,
    private val usersDataSourceRepository: UsersDataSourceRepository,
    private val tokensDataSourceRepository: TokensDataSourceRepository
) {
    suspend fun performSignIn(call: ApplicationCall, hoconApplicationConfig: HoconApplicationConfig) {
        val receive = call.receive<SignInReceiveRemote>() // получаем логин от клиента

        usersDataSourceRepository.fetchUserByEmail(
            receivedEmail = receive.email,
            onSuccess = { userDto ->
                if (userDto != null) {
                    println("::::::::::: onSuccess userDto=" + userDto)
                    if (checkToCompareTwoHashes(
                            userDto = userDto,
                            receive = receive,
                            hoconApplicationConfig = hoconApplicationConfig
                        )
                    ) {
                        findUserIdByEmail(emailFromDb = userDto.email, call = call)
                    } else {
                        runBlocking {
                            call.respond( // что будет правильным отправить клиенту
                                MessageResponse(
                                    httpStatusCode = HttpStatusCode.Conflict.value,
                                    message = "User not found"
                                )
                            )
                            return@runBlocking
                        }
                    }
                } else {
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.Conflict.value,
                                message = "User not found"
                            )
                        )
                        return@runBlocking
                    }
                }
            },
            onFailure = { e ->
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

    private fun checkToCompareTwoHashes(
        userDto: UserDTO,
        receive: SignInReceiveRemote,
        hoconApplicationConfig: HoconApplicationConfig
    ): Boolean {
        // используем функицю проверки для сравнения хешей
        return hashingService.verify(
            password = receive.password, // пароль запроса
            passwordHex = userDto.password,
            hoconApplicationConfig = hoconApplicationConfig,
        )
    }

    private fun findUserIdByEmail(emailFromDb: String, call: ApplicationCall) {
        usersDataSourceRepository.findUserIdByEmail(
            emailFromDb = emailFromDb,
            onSuccess = { userId ->
                if (userId != null) {
                    generateRefreshTokenAndInsertToDb(userIdFromDb = userId, call = call, tokenConfig = tokenConfig)
                } else {
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.Conflict.value,
                                message = "User id not found"
                            )
                        )
                        return@runBlocking
                    }
                }
            },
            onFailure = { e ->
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

    private fun generateRefreshTokenAndInsertToDb(userIdFromDb: Int, call: ApplicationCall, tokenConfig: TokenConfig) {
        val refreshToken = tokenService.generateRefreshToken(
            refreshTokenConfig = tokenConfig.getRefreshTokenConfig(),
            TokenClaim( // - объект, содержащий имя и значение утверждения (claim), которое будет добавлено в токен.
                userId = "userId",
                userIdValue = userIdFromDb
            )
        )
        // удалить рефреш токен сначала
        deleteAndInsertRefreshTokenByUserId(userId = userIdFromDb, refreshToken = refreshToken, call = call)
    }

    private fun deleteAndInsertRefreshTokenByUserId(userId: Int, refreshToken: String, call: ApplicationCall) {
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
            })
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