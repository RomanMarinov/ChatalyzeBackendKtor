package ru.marinovdev.data.messages.model

import kotlinx.serialization.Serializable

@Serializable
data class UserPairChat(
    val sender: String,
    val recipient: String
)