package ru.marinovdev.features.auth_lackner.security.token

data class TokenClaim(
    val userId: String,
    val userIdValue: Int
)
