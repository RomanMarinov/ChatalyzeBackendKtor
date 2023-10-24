package ru.marinovdev.features.auth_lackner.security.hashing_password

data class SaltedHash(
    val hashPasswordSalt: String,
    val salt: String
)
