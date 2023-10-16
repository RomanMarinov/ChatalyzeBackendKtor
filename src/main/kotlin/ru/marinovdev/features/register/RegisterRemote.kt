package ru.marinovdev.features.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReceiveRemote( // входные данные
    val email: String,
    val password: String
)

@Serializable
data class RegisterResponseRemote( // отдаем
    val accessToken: String,
    val refreshToken: String
)