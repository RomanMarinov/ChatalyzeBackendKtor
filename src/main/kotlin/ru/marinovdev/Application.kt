package ru.marinovdev

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import ru.marinovdev.features.login.configureLoginRouting
import ru.marinovdev.features.register.configureRegisterRouting
import ru.marinovdev.features.send_email.configureSenderEmailRouting
import ru.marinovdev.plugins.configureRouting
import ru.marinovdev.plugins.configureSerialization

fun main() {
    try {
        // Сконфигурировать БД
        val url = "jdbc:postgresql://localhost:5433/chatalyze_bd"
    //    val url = "jdbc:postgresql://n_car_fuel_calc_postgres:5432/car_fuel"
        val user = "admin"
        val password = "qwerty"

        Database.connect(
            url = url,
            driver = "org.postgresql.Driver",
            user = user,
            password = password
        )

        embeddedServer(
            Netty,
            port = 8080,
            host = "0.0.0.0",
            module = Application::module
        )
            .start(wait = true)
    } catch (e: Exception) {
        println("try catch fun main e=" + e)
    }


}


fun Application.module() {
    configureRouting()
    configureLoginRouting()
    configureRegisterRouting()
    configureSerialization()
    configureSenderEmailRouting()
}


//fun main() {
//    // Сконфигурировать БД
//    val url = "jdbc:postgresql://localhost:5432/car_fuel"
//    val user = "RomanMarinov"
//    val password = "123qweRT"
//    Database.connect(url, driver = "org.postgresql.Driver", user = user, password = password)
//
//    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
//        .start(wait = true)
//}
//
//fun Application.module() {
//    configureRouting()
//    configureLoginRouting()
//    configureRegisterRouting()
//    configureSerialization()
//}
