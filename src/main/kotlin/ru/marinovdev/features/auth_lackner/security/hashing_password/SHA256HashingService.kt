package ru.marinovdev.features.auth_lackner.security.hashing_password

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

class SHA256HashingService : HashingService {
    override fun generateSaltHash(password: String, saltLength: Int): SaltedHash {
        // генерация строки безопастным способом длины 32 символа
        val salt: ByteArray = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
        // кодируем 16 ричную строку
        val saltHex: String = Hex.encodeHexString(salt)
        val hashPasswordSalt = DigestUtils.sha256Hex("$password$saltHex") // это соль + пароль

        return SaltedHash(
            hashPasswordSalt = hashPasswordSalt,
            salt = saltHex
        )
    }

    //  Если вычисленное хэш-значение совпадает с хэш-значением в объекте SaltedHash,
    //  то метод возвращает true, иначе - false.
    override fun verify(password: String, saltedHash: SaltedHash): Boolean {
        return DigestUtils.sha256Hex(password + saltedHash.salt) == saltedHash.hashPasswordSalt
    }
}