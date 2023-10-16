package ru.marinovdev.features.auth_lackner.requests

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val email: String,
    val password: String
)
