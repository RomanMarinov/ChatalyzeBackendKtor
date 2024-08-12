package ru.marinovdev.features.database

import io.ktor.server.application.*
import org.koin.java.KoinJavaComponent.inject

fun Application.configureDatabase() {
    val databaseConnection: DatabaseConnection by inject(DatabaseConnection::class.java)
    databaseConnection.connectToDatabase()
}