package ru.marinovdev.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.data.firebase.model.UserFirebase
import ru.marinovdev.domain.repository.UserFirebaseRepository
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource

class FirebaseRegisterController(private val userFirebaseRepository: UserFirebaseRepository) {

    suspend fun saveUserFirebase(call: ApplicationCall) {
        try {
            val received = call.receive<UserFirebase>()

            userFirebaseRepository.checkUserFirebaseByPhone(
                userFirebase = received,
                onSuccess = { userFirebaseExists ->
                    println("::::::::::: onSuccess FirebaseRegisterController checkUserFirebaseByPhone")
                    if (userFirebaseExists) {
                        updateUserFirebase(userFirebase = received, call = call)
                    } else {
                        insertUserFirebase(userFirebase = received, call = call)
                    }
                },
                onFailure = { e ->
                    println("::::::::::: onFailure FirebaseRegisterController checkUserFirebaseByPhone e=" + e)
                }
            )
        } catch (e: Exception) {
            println("::::::::::: try catch FirebaseRegisterController checkUserFirebaseByPhone e=" + e)
        }
    }

    private fun insertUserFirebase(userFirebase: UserFirebase, call: ApplicationCall) {
        userFirebaseRepository.insertUserFirebaseToDb(
            userFirebase = userFirebase,
            onSuccess = {
                println("::::::::::: FirebaseRegisterController insertUserFirebase onSuccess")
                runBlocking {
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.OK.value,
                            message = StringResource.FIREBASE_INSERT_SUCCESS
                        )
                    )
                }
            },
            onFailure = { e ->
                println("::::::::::: FirebaseRegisterController insertUserFirebase onFailure e=" + e)
               runBlocking {
                   call.respond(
                       MessageResponse(
                           httpStatusCode = HttpStatusCode.InternalServerError.value,
                           message = StringResource.FIREBASE_INSERT_FAILURE + e.localizedMessage
                       )
                   )
               }
            }
        )
    }

    private fun updateUserFirebase(userFirebase: UserFirebase, call: ApplicationCall) {
        userFirebaseRepository.updateUserFirebaseToDb(
            userFirebase = userFirebase,
            onSuccess = {
                println("::::::::::: FirebaseRegisterController overWriteUserFirebase onSuccess")
                runBlocking {
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.OK.value,
                            message = StringResource.FIREBASE_OVER_WRITE_SUCCESS
                        )
                    )
                }
            },
            onFailure = { e ->
                println("::::::::::: FirebaseRegisterController overWriteUserFirebase onFailure e=" + e)
                runBlocking {
                    println(":::::::::::DeleteProfileController deleteUserByUserId onFailure ")
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.InternalServerError.value,
                            message = StringResource.FIREBASE_OVER_WRITE_FAILURE + e.localizedMessage
                        )
                    )
                }
            }
        )
    }
}