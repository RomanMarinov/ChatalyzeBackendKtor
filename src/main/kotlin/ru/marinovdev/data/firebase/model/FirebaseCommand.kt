package ru.marinovdev.data.firebase.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseCommand(
    val topic: String,
    @SerialName("sender_phone")
    val senderPhone: String,
    @SerialName("recipient_phone")
    val recipientPhone: String,
    @SerialName("text_message")
    val textMessage: String,
    @SerialName("type_firebase_command")
    val typeFirebaseCommand: String
)

