package ru.marinovdev.plugins

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import java.time.Duration

fun Application.configureSocketsParams() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(5) // пинг запросы каждые 15сек
        timeout = Duration.ofSeconds(15) // соединение будет считаться разорванным через 15 сек
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

}

//fun Application.configureWebSocket() {
//    val client = HttpClient {
//        install(WebSockets)
//    }
//    runBlocking {
//        // host = "0.0.0.0"
//        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/chat") {
//            val messageOutputRoutine = launch { outputMessages() }
//            val userInputRoutine = launch { inputMessages() }
//
//            userInputRoutine.join() // Wait for completion; either "exit" or error
//            messageOutputRoutine.cancelAndJoin()
//        }
//    }
//    client.close()
//    println("Connection closed. Goodbye!")
//}
//
//suspend fun DefaultClientWebSocketSession.outputMessages() {
//    try {
//        for (message in incoming) {
//            message as? Frame.Text ?: continue
//            println(message.readText())
//        }
//    } catch (e: Exception) {
//        println("Error while receiving: " + e.localizedMessage)
//    }
//}
//
//suspend fun DefaultClientWebSocketSession.inputMessages() {
//    while (true) {
//        val message = readLine() ?: ""
//        if (message.equals("exit", true)) return
//        try {
//            send(message)
//        } catch (e: Exception) {
//            println("Error while sending: " + e.localizedMessage)
//            return
//        }
//    }
//}