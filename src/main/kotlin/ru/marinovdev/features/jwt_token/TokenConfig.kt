package ru.marinovdev.features.jwt_token

import ru.marinovdev.features.auth_lackner.security.token.AccessTokenConfig
import ru.marinovdev.features.auth_lackner.security.token.RefreshTokenConfig

class TokenConfig {
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