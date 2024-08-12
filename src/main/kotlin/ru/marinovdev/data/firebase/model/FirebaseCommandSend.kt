package ru.marinovdev.data.firebase.model

data class FirebaseCommandSend(
    val firebaseToken: String,
    val topic: String,
    val senderPhone: String,
    val recipientPhone: String,
    val textMessage: String,
    val typeFirebaseCommand: String
)
