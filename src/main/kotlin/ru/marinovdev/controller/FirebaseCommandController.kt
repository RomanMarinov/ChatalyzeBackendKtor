package ru.marinovdev.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.marinovdev.data.firebase.model.FirebaseCommand
import ru.marinovdev.data.firebase.model.FirebaseCommandSend
import ru.marinovdev.domain.repository.UserFirebaseRepository
import ru.marinovdev.model.MessageResponse
import ru.marinovdev.utils.StringResource

class FirebaseCommandController(private val userFirebaseRepository: UserFirebaseRepository) {

    suspend fun executeDivideCommands(call: ApplicationCall) {
        val firebaseCommand = call.receive<FirebaseCommand>()
        when(firebaseCommand.typeFirebaseCommand) {
            StringResource.TYPE_FIREBASE_MESSAGE_MESSAGE -> {
                println(":::::::::FirebaseCommandController TYPE_FIREBASE_MESSAGE_MESSAGE  \n$firebaseCommand")
                performPushMessaging(firebaseCommand = firebaseCommand, call = call)
            }
            StringResource.TYPE_FIREBASE_MESSAGE_CALL -> {
                println(":::::::::FirebaseCommandController TYPE_FIREBASE_MESSAGE_CALL \n$firebaseCommand")
                performCalling(firebaseCommand = firebaseCommand, call = call)
            }
            StringResource.TYPE_FIREBASE_MESSAGE_READY_STREAM -> {
                println(":::::::::FirebaseCommandController TYPE_FIREBASE_MESSAGE_READY_STREAM \n$firebaseCommand")
                performStreaming(firebaseCommand = firebaseCommand, call = call)
            }
        }
    }

    private fun performPushMessaging(firebaseCommand: FirebaseCommand, call: ApplicationCall) {
        fetchUserFirebaseFromDb(firebaseCommand = firebaseCommand, call = call)
    }

    private fun performCalling(firebaseCommand: FirebaseCommand, call: ApplicationCall) {
        fetchUserFirebaseFromDb(firebaseCommand = firebaseCommand, call = call)
    }

    private fun fetchUserFirebaseFromDb(firebaseCommand: FirebaseCommand, call: ApplicationCall) {
        userFirebaseRepository.fetchUserFirebaseFromDb(
            firebaseCommand = firebaseCommand,
            onSuccess = { userFirebase ->
                println(":::::::::::::::::: FirebaseCallController fetchUserFirebaseFromDb onSuccess")
                runBlocking {
                    val firebaseCommandSend = FirebaseCommandSend(
                        firebaseToken = userFirebase.firebaseToken,
                        topic = firebaseCommand.topic,
                        senderPhone = firebaseCommand.senderPhone,
                        recipientPhone = userFirebase.registerSenderPhone,
                        textMessage = firebaseCommand.textMessage,
                        typeFirebaseCommand = firebaseCommand.typeFirebaseCommand
                    )
                    sendPushMessageCommandToFirebase(firebaseCommandSend = firebaseCommandSend, call = call)
                }
            },
            onFailure = {
                println(":::::::::::::::::: FirebaseCallController fetchUserFirebaseFromDb onFailure")
            }
        )
    }

    private fun performStreaming(firebaseCommand: FirebaseCommand, call: ApplicationCall) {
        fetchUserFirebaseFromDbForStream(firebaseCommand = firebaseCommand, call = call)
    }

    private fun fetchUserFirebaseFromDbForStream(firebaseCommand: FirebaseCommand, call: ApplicationCall) {
        userFirebaseRepository.fetchUserFirebaseFromDbForStream(
            firebaseCommand = firebaseCommand,
            onSuccess = { userFirebase ->
                println(":::::::::::::::::: FirebaseCallController fetchUserFirebaseFromDb onSuccess")
                runBlocking {
                    val firebaseCommandSend = FirebaseCommandSend(
                        firebaseToken = userFirebase.firebaseToken,
                        topic = firebaseCommand.topic,
                        senderPhone = firebaseCommand.senderPhone,
                        recipientPhone = userFirebase.registerSenderPhone,
                        textMessage = firebaseCommand.textMessage,
                        typeFirebaseCommand = firebaseCommand.typeFirebaseCommand
                    )
                    sendCallCommandToFirebase(firebaseCommandSend = firebaseCommandSend, call = call)
                }
            },
            onFailure = {
                println(":::::::::::::::::: FirebaseCallController fetchUserFirebaseFromDb onFailure")
            }
        )
    }


    private suspend fun sendCallCommandToFirebase(firebaseCommandSend: FirebaseCommandSend, call: ApplicationCall) {
        userFirebaseRepository.sendCallCommandToFirebase(
            firebaseCommandSend = firebaseCommandSend,
            onSuccess = {
                runBlocking {
                    println(":::::::::::::::::: configureFirebaseRouting onSuccess")
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.OK.value,
                            message = StringResource.FIREBASE_MAKE_CALL_SUCCESS
                        )
                    )
                    return@runBlocking
                }
            },
            onFailure = { e ->
                runBlocking {
                    println(":::::::::::::::::: configureFirebaseRouting onFailure e=" + e)
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.NotFound.value,
                            message = StringResource.FIREBASE_MAKE_CALL_FAILURE
                        )
                    )
                    return@runBlocking
                }
            }
        )
    }

    private suspend fun sendPushMessageCommandToFirebase(firebaseCommandSend: FirebaseCommandSend, call: ApplicationCall) {
        userFirebaseRepository.sendPushMessageCommandToFirebase(
            firebaseCommandSend = firebaseCommandSend,
            onSuccess = {
                runBlocking {
                    println(":::::::::::::::::: sendPushMessageCommandToFirebase onSuccess")
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.OK.value,
                            message = StringResource.FIREBASE_MAKE_PUSH_SUCCESS
                        )
                    )
                    return@runBlocking
                }
            },
            onFailure = { e ->
                runBlocking {
                    println(":::::::::::::::::: sendPushMessageCommandToFirebase onFailure e=" + e)
                    call.respond(
                        MessageResponse(
                            httpStatusCode = HttpStatusCode.NotFound.value,
                            message = StringResource.FIREBASE_MAKE_PUSH_FAILURE
                        )
                    )
                    return@runBlocking
                }
            }
        )
    }
}