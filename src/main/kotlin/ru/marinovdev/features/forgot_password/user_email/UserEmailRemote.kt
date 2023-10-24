package ru.marinovdev.features.forgot_password.user_email

import kotlinx.serialization.Serializable

@Serializable
data class UserEmailRemote(
    val email: String
)