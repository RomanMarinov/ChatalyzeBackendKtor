package ru.marinovdev

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.jetbrains.exposed.sql.Database
import ru.marinovdev.features.login.configureLoginRouting
import ru.marinovdev.features.register.configureRegisterRouting
import ru.marinovdev.plugins.*

fun main() {
    // Сконфигурировать БД
    val url = "jdbc:postgresql://localhost:5432/car_fuel"
    val user = "RomanMarinov"
    val password = "123qweRT"
    Database.connect(url, driver = "org.postgresql.Driver", user = user, password = password)

    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
    configureLoginRouting()
    configureRegisterRouting()
    configureSerialization()
}
