package ru.marinovdev.data.messages.model

import kotlinx.serialization.Serializable


@Serializable
data class Message(
    val sender: String,
    val recipient: String,
    val textMessage: String,
    val createdAt: String
)

//@Serializable
//data class Message(
//    val text: String,
//    val username: String,
//    val timestamp: Long
//)
