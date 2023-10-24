package ru.marinovdev.features.auth_lackner.security.hashing_code

data class CodeConfig(
    val issuer: String,
    val audience: String,
    val expiresIn: Long,
    val secret: String
)
