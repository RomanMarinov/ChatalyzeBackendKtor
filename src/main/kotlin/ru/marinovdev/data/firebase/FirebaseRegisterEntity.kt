package ru.marinovdev.data.firebase

import org.jetbrains.exposed.sql.Table

object FirebaseRegisterEntity : Table("firebase") {
    val id = FirebaseRegisterEntity.integer("id")
    val registerSenderPhone = FirebaseRegisterEntity.varchar("register_sender_phone", 30)
    val firebaseToken = FirebaseRegisterEntity.varchar("firebase_token", 200)
}