package ru.marinovdev.features.sign_in

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.marinovdev.database.tokens.TokenDTO
import ru.marinovdev.database.tokens.Tokens
import ru.marinovdev.database.users.UserDTO
import ru.marinovdev.database.users.Users
import ru.marinovdev.features.auth_lackner.security.hashing_password.HashingService
import ru.marinovdev.features.auth_lackner.security.hashing_password.SaltedHash
import ru.marinovdev.features.auth_lackner.security.token.AccessTokenConfig
import ru.marinovdev.features.auth_lackner.security.token.RefreshTokenConfig
import ru.marinovdev.features.auth_lackner.security.token.TokenClaim
import ru.marinovdev.features.auth_lackner.security.token.TokenService
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource

class SignInController(
    private val call: ApplicationCall,
    private val hashingService: HashingService,
    private val tokenService: TokenService,
    private val accessTokenConfig: AccessTokenConfig,
    private val refreshTokenConfig: RefreshTokenConfig
) {
    suspend fun performSignIn() {
        val receive = call.receive<SignInReceiveRemote>() // получаем логин от клиента
        println("SignInController receive=" + receive)

        Users.fetchUserByEmail(
            receivedEmail = receive.email,
            onSuccess = { userDto ->
                if (userDto != null) {
                    println("::::::::::: onSuccess userDto=" + userDto)
                    if (checkToCompareTwoHashes(userDto = userDto, receive = receive)) {
                        findUserIdByEmail(emailFromDb = userDto.email)
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
                    println("::::::::::: onSuccess userDto=" + userDto)
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

    private fun checkToCompareTwoHashes(userDto: UserDTO, receive: SignInReceiveRemote): Boolean {
        // используем функицю проверки для сравнения хешей
        return hashingService.verify(
            password = receive.password, // пароль запроса
            saltedHash = SaltedHash( // новый экз который создаем и это хешированное значение из бд
                hashPasswordSalt = userDto.password,
                salt = userDto.salt
            )
        )
    }

    private fun findUserIdByEmail(emailFromDb: String) {
        Users.findUserIdByEmail(
            emailFromDb = emailFromDb,
            onSuccess = { userId ->
                if (userId != null) {
                    generateRefreshTokenAndInsertToDb(userIdFromDb = userId)
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

    private fun generateAccessToken(userIdFromDb: Int): String {
        // тут мы уверены что юзер ввел правильный пароль
        // и генерируем  токен и прикрепить его к ответу чтобы пользователь мог сохранить его в настройках
        return tokenService.generateAccessToken(
            accessTokenConfig = accessTokenConfig, //объект конфигурации токена, содержащий настройки эмитента, аудитории, срока действия токена и секретный ключ для подписи токена.
            TokenClaim( // - объект, содержащий имя и значение утверждения (claim), которое будет добавлено в токен.
                userId = "userId",
                userIdValue = userIdFromDb
            )
        )
    }

    private fun generateRefreshTokenAndInsertToDb(userIdFromDb: Int) {
        val refreshToken = tokenService.generateRefreshToken(
            refreshTokenConfig = refreshTokenConfig,
            TokenClaim( // - объект, содержащий имя и значение утверждения (claim), которое будет добавлено в токен.
                userId = "userId",
                userIdValue = userIdFromDb
            )
        )

        // удалить рефреш токен сначала



        Tokens.insertRefreshToken(TokenDTO(
            userId = userIdFromDb,
            refreshToken = refreshToken
        ),
            onSuccess = {
                println("::::::::::: onSuccess insertRefreshToken")
                fetchRefreshTokenByUserId(userId = userIdFromDb)
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
    }

    private fun fetchRefreshTokenByUserId(userId: Int) {
        Tokens.fetchRefreshTokenByUserId(
            userId = userId,
            onSuccess = { refreshToken ->
                if (refreshToken != null) {
                    val accessToken = generateAccessToken(userIdFromDb = userId)
                    sendingTokenToClient(accessToken = accessToken, refreshToken = refreshToken)
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

    private fun sendingTokenToClient(accessToken: String, refreshToken: String) {
        println(":::::::::::sendingTokenToClient accessToken=" + accessToken)
        println(":::::::::::sendingTokenToClient refreshToken=" + refreshToken)
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