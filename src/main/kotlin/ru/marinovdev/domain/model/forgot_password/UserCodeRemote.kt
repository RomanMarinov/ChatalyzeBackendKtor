package ru.marinovdev.domain.model.forgot_password

import kotlinx.serialization.Serializable

@Serializable
data class UserCodeRemote(
    val email: String,
    val code: Int
)
