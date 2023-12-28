package ru.marinovdev.controller

import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.marinovdev.data.user_manager.UserSocketManager
import ru.marinovdev.data.users_session.OnlineUserState
import ru.marinovdev.data.users_session.dto.MessageWrapper
import ru.marinovdev.data.users_session.dto.OnlineUsers
import ru.marinovdev.domain.repository.MessageDataSourceRepository
import ru.marinovdev.domain.repository.UserSessionDataSourceRepository

class SocketStateUserController(
    private val messageDataSourceRepository: MessageDataSourceRepository,
    private val userSessionDataSourceRepository: UserSessionDataSourceRepository
) {
    suspend fun getStateUsersConnect() {

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

            // джоба для того чтобы проверить есть ли сессия для юзеров
            job = scope.launch {
                while (true) {
                    println("Выполняется job")
                    UserSocketManager.getAllUser().forEach {
                        println("getAllUserSocket().forEach it=" + it)

                        checkUserSessionExists(userPhone = it.userPhone) // закидываю всех
                        delay(1L)
                    }
                    delay(5000L)
                }
            }
        }
    }

    // проверяем равен ли userPhone в UserSocketConnectionEntity userPhone
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
                println(":::::::::::::::::SocketMessageController updateUserSession insertUserSession onFailure  e=" + e)
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
                println(":::::::::::::::::SocketMessageController insertUserSession insertUserSession onFailure  e=" + e)
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
                sendPing(userPhone = userPhone, listOnlineUserState = listOnlineOrDate)
            },
            onFailure = { e ->
                println(":::::::::::::::::SocketMessageController getListOnlineOrDate onFailure  e=" + e)
            }
        )
    }

    private fun sendPing(userPhone: String, listOnlineUserState: List<OnlineUserState>) {
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



        val list = listOf(
            OnlineUserState(
                userPhone = "9203333333",
                onlineOrDate = "online"
            ),
            OnlineUserState(
                userPhone = "9303454564",
                onlineOrDate = "offline"
            )
        )



//        val list = listOf(
//            OnlineUserState(
//                userPhone = "9203333333",
//                onlineOrDate = "online"
//            ),
//            OnlineUserState(
//                userPhone = "9303454564",
//                onlineOrDate = "offline"
//            )
//        )


        val json = Json { encodeDefaults = true }
        //json.encodeToString(TestClass("text"))
        val onlineUsers = OnlineUsers(list)






        println(":::::::::::::::::SocketMessageController list=" + list)
        scope.launch {
            println(":::::::::::::::::SocketMessageController sendPing")
            UserSocketManager.getAllUser().forEach { userSocket ->
                if (userSocket.userPhone == userPhone) {
                    println(":::::::::::::::::SocketMessageController отправка юзеру=" + userPhone)
//                    send(Frame.Ping("Ping еп тить".toByteArray())) // Отправляем ping-фрейм


                    val listJson = Json.encodeToString(list)

                    val listText = Json.encodeToString(
                        MessageWrapper(
                            type = "userList",
                            payloadJson = listJson
                        )
                    )

                  //  val parsedMessage = Json.encodeToString(list)
//                    val parsedMessage = Json.encodeToString(listOnlineOrDate)
                   // println(":::::::::::::::::SocketMessageController parsedMessage=" + parsedMessage)
                    //userSocket.socket.send(Frame.Text(parsedMessage))
                  //  userSocket.socket.send("parsedMessage   ereverveverve")
                    userSocket.socket.send(Frame.Text(listText))
                    // после энкода
                    // [{"userPhone":"9203333333","onlineOrDate":"online"},{"userPhone":"9303454564","onlineOrDate":"offline"}]

//                    val deserializedList = Json.decodeFromString<List<OnlineUserState>>(parsedMessage)
//                    println("Deserialized List deserializedList=" + deserializedList)

                    // после декода
                    // [OnlineUserState(userPhone=9203333333, onlineOrDate=online), OnlineUserState(userPhone=9303454564, onlineOrDate=offline)]

//                    deserializedList.forEach { dto ->
//                        println("User Phone: ${dto.userPhone}, Online or Date: ${dto.onlineOrDate}")
//                    }


                 //   userSocket.socket.send(Frame.Ping("Ping еп тить".toByteArray())) // Отправляем ping-фрейм
                    return@forEach
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

//    fun <I> encodeToString(type: KType, model: I): String {
//        return Json.Default.encodeToString(Json.serializersModule.serializer(type), model)
//    }

}


//private fun sendPing(userPhone: String, listOnlineUserState: List<OnlineUserState>) {
//    val parsedMessage = Json.encodeToString(listOnlineUserState)
////                    val parsedMessage = Json.encodeToString(listOnlineOrDate)
//    userSocket.socket.send(Frame.Text(parsedMessage))
//
//}