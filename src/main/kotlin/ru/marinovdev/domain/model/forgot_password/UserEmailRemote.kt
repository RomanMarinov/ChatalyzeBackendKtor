package ru.marinovdev.domain.model.forgot_password

import kotlinx.serialization.Serializable

@Serializable
data class UserEmailRemote(
    val email: String
)