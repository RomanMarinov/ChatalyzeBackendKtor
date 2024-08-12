package ru.marinovdev.data.firebase.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserFirebase(
    @SerialName("register_sender_phone")
    val registerSenderPhone: String,
    @SerialName("firebase_token")
    val firebaseToken: String
)