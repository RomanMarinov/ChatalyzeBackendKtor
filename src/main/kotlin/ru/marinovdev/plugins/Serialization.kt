package ru.marinovdev.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*


fun Application.configureSerialization() {
    install(ContentNegotiation) { // отвечает за обработку и выбор правильного конвертера контента
        json() // активирует поддержку сериализации в формате JSON
    }
}