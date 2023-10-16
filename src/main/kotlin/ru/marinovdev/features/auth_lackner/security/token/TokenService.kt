package ru.marinovdev.features.auth_lackner.security.token

interface TokenService {
    fun generateAccessToken(accessTokenConfig: AccessTokenConfig, vararg tokenClaim: TokenClaim): String
    fun generateRefreshToken(refreshTokenConfig: RefreshTokenConfig, vararg tokenClaim: TokenClaim): String
    fun decodeRefreshToken(refreshToken: String) : TokenPayload
}