package ru.marinovdev.features.auth_lackner.security.hashing_password

interface HashingService {
    fun generateSaltHash(password: String, saltLength: Int = 32) : SaltedHash
    // saltedHash отоортированный хеш который мы получаем из бд
    fun verify(password: String, saltedHash: SaltedHash) : Boolean
}