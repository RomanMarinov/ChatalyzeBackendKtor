package ru.marinovdev.data.user_manager

import io.ktor.websocket.*
import ru.marinovdev.data.socket_connection.Member
import java.util.concurrent.ConcurrentHashMap

object UserSocketManager {
    private val userSocket = ConcurrentHashMap<String, Member>()

    fun addUser(userPhone: String, sessionId: String, socket: WebSocketSession) {
        val member = Member(userPhone, sessionId, socket)
        userSocket[sessionId] = member
    }

    fun getUserSocket(userPhone: String) : WebSocketSession? {
        return userSocket[userPhone]?.socket
    }

    fun getAllUser() : List<Member> {
        return userSocket.values.toList()
    }

    fun deleteUser(username: String) {
        userSocket.remove(username)
    }

    fun checkContainsUser(userPhone: String) : Boolean {
        return userSocket.contains(userPhone)
    }
}