package ru.marinovdev.data.user_manager

import io.ktor.websocket.*
import ru.marinovdev.data.socket_connection.Member
import java.util.concurrent.ConcurrentHashMap

object UserSocketManager {
    private val userSocket = ConcurrentHashMap<String, Member>()

    fun addUser(userPhone: String, sessionId: String, socket: WebSocketSession) {
        val member = Member(userPhone, sessionId, socket)
        userSocket[userPhone] = member
    }

    fun getUserSocket(userPhone: String) : WebSocketSession? {
        if (userSocket[userPhone]?.socket == null) {
            println("::::::::::getUserSocket socket == null ")
        } else {
            println("::::::::::getUserSocket socket != null ")
        }

        println("::::::::::")
        return userSocket[userPhone]?.socket
    }

    fun getAllUser() : List<Member> {
        return userSocket.values.toList()
    }

    // еще вариант удаления
//    fun deleteUser(userPhone: String) {
//        userSocket.remove(userPhone)
//        userSocket[userPhone]
//    }

    fun removeMemberByUserPhone(userPhone: String) {
        // Ищем ключ, соответствующий заданному userPhone
        val keyToRemove = userSocket.filterValues { it.userPhone == userPhone }.keys.firstOrNull()
        // Если ключ найден, удаляем элемент из map
        keyToRemove?.let {
            userSocket.remove(it)
        }
    }

    fun checkContainsUser(userPhone: String) : Boolean {
        return userSocket.contains(userPhone)
    }
}