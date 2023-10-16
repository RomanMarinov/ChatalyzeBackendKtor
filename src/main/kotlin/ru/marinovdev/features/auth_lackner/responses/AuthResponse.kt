package ru.marinovdev.features.auth_lackner.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String
)
