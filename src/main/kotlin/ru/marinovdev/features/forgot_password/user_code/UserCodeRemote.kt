package ru.marinovdev.features.forgot_password.user_code

import kotlinx.serialization.Serializable

@Serializable
data class UserCodeRemote(
    val email: String,
    val code: Int
)
