package ru.marinovdev.features.auth_lackner.security.token

data class TokenPayload(
    val userId: Int?,
    val expiresIn: Long?,
)
