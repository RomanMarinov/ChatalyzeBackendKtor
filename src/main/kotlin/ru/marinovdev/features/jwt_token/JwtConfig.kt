package ru.marinovdev.features.jwt_token

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

object JwtConfig {
    fun getIssuer(): String {
        return HoconApplicationConfig(ConfigFactory.load()).property("jwt.issuer").getString()
    }

    fun getAudience(): String {
        return HoconApplicationConfig(ConfigFactory.load()).property("jwt.audience").getString()
    }

    fun getSecret(): String {
        return HoconApplicationConfig(ConfigFactory.load()).property("jwt.secret").getString()
    }

    fun getRealm(): String {
        return HoconApplicationConfig(ConfigFactory.load()).property("jwt.realm").getString()
    }

    fun getSalt(): String {
        return HoconApplicationConfig(ConfigFactory.load()).property("jwt.salt").getString()
    }
}