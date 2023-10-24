package ru.marinovdev.features.forgot_password.user_password

import kotlinx.serialization.Serializable

@Serializable
data class UserPasswordRemote(
    val email: String,
    val password: String
)
