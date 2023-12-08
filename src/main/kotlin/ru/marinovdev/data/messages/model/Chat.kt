package ru.marinovdev.data.messages.model

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val sender: String,
    val recipient: String,
    val text_message: String,
    val created_at: String
)

