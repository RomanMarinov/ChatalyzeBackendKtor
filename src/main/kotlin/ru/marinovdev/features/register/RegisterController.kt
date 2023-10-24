package ru.marinovdev.features.register

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.database.users.UserDTO
import ru.marinovdev.database.users.Users
import ru.marinovdev.features.auth_lackner.security.hashing_password.HashingService
import ru.marinovdev.features.auth_lackner.security.hashing_password.SaltedHash
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource


class RegisterController(
    private val call: ApplicationCall,
    private val hashingService: HashingService
) {
    suspend fun registerUser() {
        try {
            val registerReceiveRemote = call.receive<RegisterReceiveRemote>() // получаем email от клиента
            Users.checkEmailExists(
                emailFromDb = registerReceiveRemote.email,
                onSuccess = { isEmailExists ->
                    if (isEmailExists) {
                        runBlocking {
                            println(":::::::::::registerNewUser onSuccess isEmailExists=" + isEmailExists + " уже существует")
                            call.respond(
                                MessageResponse(
                                    httpStatusCode = HttpStatusCode.Conflict.value,
                                    message = StringResource.USER_ALREADY_EXISTS
                                )
                            )
                            return@runBlocking
                        }
                    } else {
                        println(":::::::::::registerNewUser onSuccess isEmailExists=" + isEmailExists + " пока не существует")
                        insertUserIntoDatabase(
                            registerReceiveRemote = registerReceiveRemote,
                            hashingService = hashingService
                        )
                    }
                },
                onFailure = {
                    println(":::::::::::registerNewUser onFailure isEmailExists")
                    runBlocking {
                        call.respond(
                            MessageResponse(
                                httpStatusCode = HttpStatusCode.InternalServerError.value,
                                message = StringResource.CHECK_EMAIL_EXISTS_ERROR
                            )
                        )
                    }
                }
            )
        } catch (e: Exception) {
            println(":::::::::::registerNewUser USER_REGISTRATION_ERROR")
            runBlocking {
                call.respond(
                    MessageResponse(
                        httpStatusCode = HttpStatusCode.BadRequest.value,
                        message = StringResource.USER_REGISTRATION_ERROR + e.localizedMessage
                    )
                )
            }
        }
    }

    private fun insertUserIntoDatabase(
        registerReceiveRemote: RegisterReceiveRemote,
        hashingService: HashingService
    ) {

        // сгенерировать отсортировнный хеш и пароль пользователя
        val saltHash: SaltedHash = hashingService.generateSaltHash(password = registerReceiveRemote.password)
   //     println(":::::::::::insertUserIntoDatabase saltHash.hash=" + saltHash.hash)
   //     println(":::::::::::insertUserIntoDatabase saltHash.salt=" + saltHash.salt)
// :::::::::::insertUserIntoDatabase saltHash.hash=13a0008b4f0a27223aed720b85430f6a27cd8ffe1995239628c78b98e2dc2d8c
// :::::::::::insertUserIntoDatabase saltHash.salt=d65be3bdaf3a9207ea83301de02c13573872162b9e31816b3b5bd84b93e4a77d

        Users.insertUser(
            userDTO = UserDTO(
                email = registerReceiveRemote.email,
                password = saltHash.hashPasswordSalt,
                salt = saltHash.salt,
            ),
            onSuccess = {
                println(":::::::::::insertUserIntoDatabase onSuccess")
                runBlocking {
                    call.respond(
                        MessageResponse(
                            HttpStatusCode.OK.value,
                            StringResource.USER_HAS_SUCCESSFULLY_REGISTERED
                        )
                    )
                    return@runBlocking
                }
                return@insertUser
            },
            onFailure = { e ->
                println(":::::::::::insertUserIntoDatabase onFailure=" + e)
                runBlocking {
                    call.respond(
                        MessageResponse(
                            HttpStatusCode.InternalServerError.value,
                            StringResource.USER_CREATION_ERROR + e.localizedMessage
                        )
                    )
                }
            }
        )
    }
}


//class RegisterController(
//    private val call: ApplicationCall) {
//
//    suspend fun registerUser() {
//        try {
//            val registerReceiveRemote = call.receive<RegisterReceiveRemote>() // получаем email от клиента
//
//            Users.checkEmailExists(
//                emailFromDb = registerReceiveRemote.email,
//                onSuccess = { isEmailExists ->
//                    if (isEmailExists) {
//                        runBlocking {
//                            println(":::::::::::registerNewUser onSuccess isEmailExists=" + isEmailExists + " уже существует")
//                            call.respond(
//                                MessageResponse(
//                                    httpStatusCode = HttpStatusCode.Conflict.value,
//                                    message = StringResource.USER_ALREADY_EXISTS
//                                )
//                            )
//                            return@runBlocking
//                        }
//                    } else {
//                        println(":::::::::::registerNewUser onSuccess isEmailExists=" + isEmailExists + " пока не существует")
//                        insertUserIntoDatabase(registerReceiveRemote = registerReceiveRemote)
//                    }
//                },
//                onFailure = {
//                    println(":::::::::::registerNewUser onFailure isEmailExists")
//                    runBlocking {
//                        call.respond(
//                            MessageResponse(
//                                httpStatusCode = HttpStatusCode.InternalServerError.value,
//                                message = StringResource.CHECK_EMAIL_EXISTS_ERROR
//                            )
//                        )
//                    }
//                }
//            )
//        } catch (e: Exception) {
//            println(":::::::::::registerNewUser USER_REGISTRATION_ERROR")
//            runBlocking {
//                call.respond(
//                    MessageResponse(
//                        httpStatusCode = HttpStatusCode.BadRequest.value,
//                        message = StringResource.USER_REGISTRATION_ERROR + e.localizedMessage
//                    )
//                )
//            }
//        }
//    }
//
//    private fun insertUserIntoDatabase(registerReceiveRemote: RegisterReceiveRemote) {
//        Users.insertUser(
//            userDTO = UserDTO(
//                email = registerReceiveRemote.email,
//                password = registerReceiveRemote.password
//            ),
//            onSuccess = {
//                println(":::::::::::insertUserIntoDatabase onSuccess")
//                insertTokenIntoDataBaseAndSend(registerReceiveRemote = registerReceiveRemote)
//            },
//            onFailure = { e ->
//                println(":::::::::::insertUserIntoDatabase onFailure=" + e)
//                runBlocking {
//                    call.respond(
//                        MessageResponse(
//                            HttpStatusCode.InternalServerError.value,
//                            StringResource.USER_CREATION_ERROR + e.localizedMessage
//                        )
//                    )
//                }
//            }
//        )
//    }
//
//    private fun insertTokenIntoDataBaseAndSend(registerReceiveRemote: RegisterReceiveRemote) {
//
//        val token = UUID.randomUUID().toString()
//        Tokens.insertToken(
//            tokenDTO = TokenDTO(
//                email = registerReceiveRemote.email,
//                token = token
//            ),
//            onSuccess = {
//                runBlocking {
//                    val registerResponseRemoteJson = Json.encodeToString(RegisterResponseRemote(token = token))
//                    println(":::::::::::insertTokenIntoDataBaseAndSend onSuccess")
//                    call.respond(
//                        MessageResponse(
//                            HttpStatusCode.OK.value,
//                            message = registerResponseRemoteJson
//                        )
//                    )
//                }
//            },
//            onFailure = { e ->
//                println(":::::::::::insertTokenIntoDataBaseAndSend onFailure=" + e)
//                runBlocking {
//                    call.respond(
//                        MessageResponse(
//                            HttpStatusCode.InternalServerError.value,
//                            StringResource.TOKEN_CREATION_ERROR + e.localizedMessage
//                        )
//                    )
//                }
//            }
//        )
//    }
//}