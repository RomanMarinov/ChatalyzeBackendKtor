package ru.marinovdev.features.auth_lackner.security.hashing_code

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

class SHA256HashingCodeService : HashingCodeService {
    override fun generateSaltHashCode(code: String, saltLength: Int): SaltedHashCode {
        // генерация строки безопастным способом длины 32 символа
        val salt: ByteArray = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
        // кодируем 16 ричную строку
        val saltHex: String = Hex.encodeHexString(salt)
        val hashPasswordSalt = DigestUtils.sha256Hex("$code$saltHex") // это соль + пароль

        return SaltedHashCode(
            hashCodeSalt = hashPasswordSalt,
            salt = saltHex
        )
    }

    //  Если вычисленное хэш-значение совпадает с хэш-значением в объекте SaltedHash,
    //  то метод возвращает true, иначе - false.
    override fun verifyCode(code: String, saltedHash: SaltedHashCode): Boolean {
        return DigestUtils.sha256Hex(code + saltedHash.salt) == saltedHash.hashCodeSalt
    }
}