package ru.marinovdev.data.socket_connection

import io.ktor.websocket.*

data class Member(
    val userPhone: String,
    val sessionId: String,
    val socket: WebSocketSession
)
