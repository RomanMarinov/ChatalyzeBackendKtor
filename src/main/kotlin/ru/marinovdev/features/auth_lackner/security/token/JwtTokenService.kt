package ru.marinovdev.features.auth_lackner.security.token

interface JwtTokenService {
    fun generateAccessToken(accessTokenConfig: AccessTokenConfig, vararg tokenClaim: TokenClaim): String
    fun generateRefreshToken(refreshTokenConfig: RefreshTokenConfig, vararg tokenClaim: TokenClaim): String
    fun decodeToken(token: String) : TokenPayload
    fun checkCorrectSecret(accessToken: String) : Boolean
}