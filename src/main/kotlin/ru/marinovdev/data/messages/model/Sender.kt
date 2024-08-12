package ru.marinovdev.data.messages.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sender(
    @SerialName("sender")
    val sender: String,
    @SerialName("refresh_token")
    val refreshToken: String
)
