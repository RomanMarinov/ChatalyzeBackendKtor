package ru.marinovdev.domain.model.forgot_password

import kotlinx.serialization.Serializable

@Serializable
data class UserPasswordRemote(
    val email: String,
    val password: String
)
