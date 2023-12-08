package ru.marinovdev.domain.model.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReceiveRemote(
    val email: String,
    val password: String
)