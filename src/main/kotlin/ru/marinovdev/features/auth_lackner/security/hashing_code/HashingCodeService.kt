package ru.marinovdev.features.auth_lackner.security.hashing_code

interface HashingCodeService {
    fun generateSaltHashCode(code: String, saltLength: Int = 32) : SaltedHashCode
    fun verifyCode(code: String, saltedHash: SaltedHashCode) : Boolean
}