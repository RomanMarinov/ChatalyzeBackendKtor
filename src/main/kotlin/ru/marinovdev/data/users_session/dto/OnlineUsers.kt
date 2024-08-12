package ru.marinovdev.data.users_session.dto

import kotlinx.serialization.Serializable


@Serializable
data class MessageWrapper(
    val type: String,
    val payloadJson: String
)