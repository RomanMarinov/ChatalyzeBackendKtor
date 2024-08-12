package ru.marinovdev.features.auth_lackner.security.hashing_password

import io.ktor.server.config.*
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import ru.marinovdev.features.jwt_token.JwtConfig

class HashingServiceImpl : HashingService {
    override fun generatePasswordHex(
        password: String,
        saltLength: Int,
        hoconApplicationConfig: HoconApplicationConfig
    ): String {
        // генерация строки безопастным способом длины 32 символа
        //val salt: ByteArray = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
        // кодируем 16 ричную строку
        val salt: ByteArray = JwtConfig.getSalt().toByteArray(Charsets.UTF_8)
        val saltEncoded: String = Hex.encodeHexString(salt)
        return DigestUtils.sha256Hex("$password$saltEncoded")
    }

    override fun verify(password: String, passwordHex: String, hoconApplicationConfig: HoconApplicationConfig): Boolean {
        val salt: ByteArray = JwtConfig.getSalt().toByteArray(Charsets.UTF_8)
        val saltEncoded: String = Hex.encodeHexString(salt)

        return DigestUtils.sha256Hex("$password$saltEncoded") == passwordHex
    }
}