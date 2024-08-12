package ru.marinovdev.plugins

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import java.time.Duration

fun Application.configureSocketsParams() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(5) // пинг запросы каждые 15сек
        timeout = Duration.ofSeconds(20) // соединение будет считаться разорванным через 15 сек
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}