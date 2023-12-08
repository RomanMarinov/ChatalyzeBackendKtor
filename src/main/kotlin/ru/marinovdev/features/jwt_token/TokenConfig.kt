package ru.marinovdev.features.jwt_token

import ru.marinovdev.features.auth_lackner.security.token.AccessTokenConfig
import ru.marinovdev.features.auth_lackner.security.token.RefreshTokenConfig

class TokenConfig {
//    private val issuer = HoconApplicationConfig(ConfigFactory.load()).property("jwt.issuer").getString()
//    private val audience = HoconApplicationConfig(ConfigFactory.load()).property("jwt.audience").getString()
//    private val secret = HoconApplicationConfig(ConfigFactory.load()).property("jwt.secret").getString()

    fun getAccessTokenConfig(): AccessTokenConfig {
        return AccessTokenConfig(
            issuer = JwtConfig.getIssuer(),
            audience = JwtConfig.getAudience(),
            expiresIn = 1L * 24L * 60L * 60L * 1000L,
            secret = JwtConfig.getSecret()
        )
    }

    fun getRefreshTokenConfig() : RefreshTokenConfig {
        return RefreshTokenConfig(
            issuer = JwtConfig.getIssuer(),
            audience = JwtConfig.getAudience(),
            expiresIn = 30L * 24L * 60L * 60L * 1000L,
            secret = JwtConfig.getSecret()
        )
    }
}