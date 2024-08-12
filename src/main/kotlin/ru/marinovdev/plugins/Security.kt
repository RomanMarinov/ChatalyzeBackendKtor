package ru.marinovdev.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import ru.marinovdev.data.socket_connection.ChatSession

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<ChatSession>("SESSION")
    }
    println(":::::::::::::::::::configureSecurity session")
    intercept(ApplicationCallPipeline.Features) {
        if(call.sessions.get<ChatSession>() == null) {
            val userPhone = call.parameters["sender"] ?: "Guest"
            call.sessions.set(ChatSession(sender = userPhone, sessionId = generateNonce()))
        }
    }
}