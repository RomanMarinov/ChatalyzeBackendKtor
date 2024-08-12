package ru.marinovdev.data.messages.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompanion(
    val sender_phone: String,
    val companion_phone: String
)
