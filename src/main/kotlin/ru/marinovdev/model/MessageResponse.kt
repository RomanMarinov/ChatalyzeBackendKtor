package ru.marinovdev.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val httpStatusCode: Int,
    val message: String
)
