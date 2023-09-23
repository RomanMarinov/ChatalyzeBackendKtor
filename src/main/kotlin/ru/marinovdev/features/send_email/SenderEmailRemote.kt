package ru.marinovdev.features.send_email

import kotlinx.serialization.Serializable

@Serializable
data class SenderEmailReceiveRemote( // входные данные
    val email: String
)

@Serializable
data class SenderEmailResponseRemote( // отдаем
    val response: String
)
