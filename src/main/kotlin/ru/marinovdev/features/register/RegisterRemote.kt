package ru.marinovdev.features.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReceiveRemote( // входные данные
    val login: String,
    val email: String,
    val password: String
)

@Serializable
data class RegisterResponseRemote( // отдаем
    val token: String
)
