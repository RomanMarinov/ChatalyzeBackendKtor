package ru.marinovdev.features.auth_lackner.security.hashing_code

data class SaltedHashCode(
    val hashCodeSalt: String,
    val salt: String
)
