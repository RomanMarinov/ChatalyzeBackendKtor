package ru.marinovdev.features.auth_lackner.security.token

data class AccessTokenConfig(
    val issuer: String, // эмитент
    val audience: String,
    val expiresIn: Long, // срок действия токена
    val secret: String // это клиенту нельзя знать
)
