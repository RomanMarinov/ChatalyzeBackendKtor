package ru.marinovdev.features.auth_lackner.security.hashing

data class SaltedHash(
    val hashPasswordSalt: String,
    val salt: String
)
