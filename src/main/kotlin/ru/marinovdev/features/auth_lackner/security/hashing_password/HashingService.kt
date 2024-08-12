package ru.marinovdev.features.auth_lackner.security.hashing_password

import io.ktor.server.config.*

interface HashingService {
    fun generatePasswordHex(password: String, saltLength: Int = 32, hoconApplicationConfig: HoconApplicationConfig) : String
    // saltedHash отоортированный хеш который мы получаем из бд
    fun verify(password: String, passwordHex: String, hoconApplicationConfig: HoconApplicationConfig) : Boolean
}