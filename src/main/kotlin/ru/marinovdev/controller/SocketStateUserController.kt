package ru.marinovdev.controller

import io.ktor.server.application.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.marinovdev.data.user_manager.UserSocketManager
import ru.marinovdev.data.users_session.OnlineOrDate
import ru.marinovdev.domain.repository.MessageDataSourceRepository
import ru.marinovdev.domain.repository.UserSessionDataSourceRepository

class SocketStateUserController(
    private val messageDataSourceRepository: MessageDataSourceRepository,
    private val userSessionDataSourceRepository: UserSessionDataSourceRepository
) {
    suspend fun getStateUsersConnect(call: ApplicationCall) {

        val scope = CoroutineScope(Dispatchers.IO)
        var job: Job? = null
        scope.launch {
            if (job != null && job?.isActive == true) {
                println("Coroutine is already active")
                return@launch
            }

//            job = scope.launch {
//                while (true) {
//                    println("Выполняется job")
//                    val users = UserSocketManager.getAllUserSocket() // Получаем список пользователей
//                    val tasks = users.map { user ->
//                        async {
//                            println("getAllUserSocket().forEach it=" + user)
//                            checkUserSessionExists(userPhone = user.username) // Проверяем сеанс пользователя
//                        }
//                    }
//                    tasks.awaitAll()   // Дожидаемся завершения всех операций
//                    delay(5000L)
//                }
//            }

            job = scope.launch {
                while (true) {
                    println("Выполняется job")
                    UserSocketManager.getAllUser().forEach {
                        println("getAllUserSocket().forEach it=" + it)

                        checkUserSessionExists(userPhone = it.username) // закидываю всех
                        delay(1L)
                    }
                    delay(5000L)
                }
            }
        }
    }

    private fun checkUserSessionExists(userPhone: String) {
        userSessionDataSourceRepository.checkUserSessionExists(
            userPhone = userPhone,
            onSuccess = { userSessionExists ->
                println(":::::::::::::::::SocketMessageController checkUserSessionExists onSuccess userSessionExists=" + userSessionExists)
                if (userSessionExists) {
                    updateUserSession(userPhone = userPhone)
                } else {
                    insertUserSession(userPhone = userPhone)
                }
            },
            onFailure = { e ->
                println(":::::::::::::::::SocketMessageController checkUserSessionExists onFailure  e=" + e)
            }
        )
    }

    private fun updateUserSession(userPhone: String) {
        val online = "online"
        userSessionDataSourceRepository.updateUserSession(
            userPhone = userPhone,
            onlineOrDate = online,
            onSuccess = {
                println(":::::::::::::::::SocketMessageController insertUserSession onSuccess")
                getListRecipientInDialogWithUserPhone(userPhone = userPhone)
            },
            onFailure = { e ->
                println(":::::::::::::::::SocketMessageController insertUserSession onFailure  e=" + e)
            }
        )
    }

    private fun insertUserSession(userPhone: String) {
        val online = "online"
        userSessionDataSourceRepository.insertUserSession(
            userPhone = userPhone,
            onlineOrDate = online,
            onSuccess = {
                println(":::::::::::::::::SocketMessageController insertUserSession onSuccess")
                getListRecipientInDialogWithUserPhone(userPhone = userPhone)
            },
            onFailure = { e ->
                println(":::::::::::::::::SocketMessageController insertUserSession onFailure  e=" + e)
            }
        )
    }

    private fun getListRecipientInDialogWithUserPhone(userPhone: String) {
        messageDataSourceRepository.getListRecipientInDialogWithUserPhone(
            userPhone = userPhone,
            onSuccess = { listRecipient ->
                println(":::::::::::::::::SocketMessageController getListPairDialog onSuccess listRecipient=" + listRecipient)
                if (listRecipient.isNotEmpty()) {
                    getListOnlineOrDate(listRecipient = listRecipient, userPhone = userPhone)
                }
            },
            onFailure = { e ->
                println(":::::::::::::::::SocketMessageController getListPairDialog onFailure  e=" + e)
            }
        )
    }

    private fun getListOnlineOrDate(listRecipient: List<String>, userPhone: String) {
        userSessionDataSourceRepository.getListOnlineOrDate(
            listRecipient = listRecipient,
            onSuccess = { listOnlineOrDate ->
                println(":::::::::::::::::SocketMessageController getListOnlineOrDate onSuccess  listOnlineOrDate=" + listOnlineOrDate)
                sendPing(userPhone = userPhone, listOnlineOrDate = listOnlineOrDate)
            },
            onFailure = { e ->
                println(":::::::::::::::::SocketMessageController getListOnlineOrDate onFailure  e=" + e)
            }
        )
    }

    private fun sendPing(userPhone: String, listOnlineOrDate: List<OnlineOrDate>) {
//        val socketJsonObject = Json.decodeFromString<JsonObject>(userSession.userSocketJson)
//        val socket = socketJsonObject.toString()

        // val listOnlineOrDateJson = Json.encodeToString(listOnlineOrDate)
        val scope = CoroutineScope(Dispatchers.IO)
//        scope.launch {
//            println(":::::::::::::::::SocketMessageController sendPing")
//            UserSocketManager.getAllUserSocket().forEach { userSocket ->
//                if (userSocket.username == userPhone) {
//                    val parsedMessage = Json.encodeToString(listOnlineOrDate)
//                    userSocket.socket.send(Frame.Text(parsedMessage))
//                }
//            }
//        }
        scope.launch {
            println(":::::::::::::::::SocketMessageController sendPing")
            UserSocketManager.getAllUser().forEach { userSocket ->
                if (userSocket.username == userPhone) {
                    println(":::::::::::::::::SocketMessageController отправка юзеру=" + userPhone)
//                    send(Frame.Ping("Ping еп тить".toByteArray())) // Отправляем ping-фрейм
                    val parsedMessage = Json.encodeToString(listOnlineOrDate)
                    userSocket.socket.send(Frame.Text(parsedMessage))
                 //   userSocket.socket.send(Frame.Ping("Ping еп тить".toByteArray())) // Отправляем ping-фрейм
                }
            }
        }
    }


//    suspend fun tryDisconnect(username: String) {
//        members[username]?.socket?.close()
//        if (members.containsKey(username)) {
//            members.remove(username)
//        }
//    }

}