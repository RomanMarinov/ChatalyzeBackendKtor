package ru.marinovdev.features.database

import org.jetbrains.exposed.sql.Database

class DatabaseConnection {
    fun connectToDatabase() {
        try {
            // Сконфигурировать БД
            val url = "jdbc:postgresql://localhost:5433/chatalyze_bd"
            val user = "admin"
            val password = "qwerty"

            Database.connect(
                url = url,
                driver = "org.postgresql.Driver",
                user = user,
                password = password
            )
        } catch (e: Exception) {
            println("try catch fun configureDatabase e=" + e)
        }
    }
}
