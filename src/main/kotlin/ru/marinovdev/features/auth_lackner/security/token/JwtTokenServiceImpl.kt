package ru.marinovdev.features.auth_lackner.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.serialization.json.*
import java.util.*

class JwtTokenServiceImpl : JwtTokenService {
    override fun generateAccessToken(accessTokenConfig: AccessTokenConfig, vararg tokenClaim: TokenClaim): String {
       var accessToken = JWT.create()
           .withAudience(accessTokenConfig.audience)
           .withIssuer(accessTokenConfig.issuer)
           .withExpiresAt(Date(System.currentTimeMillis() + accessTokenConfig.expiresIn))

        tokenClaim.forEach {
            accessToken = accessToken.withClaim(it.userId, it.userIdValue)
        }
        return accessToken.sign(Algorithm.HMAC256(accessTokenConfig.secret))
    }

    override fun generateRefreshToken(refreshTokenConfig: RefreshTokenConfig, vararg tokenClaim: TokenClaim): String {
        var refreshToken = JWT.create()
            .withAudience(refreshTokenConfig.audience)
            .withIssuer(refreshTokenConfig.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenConfig.expiresIn))

        tokenClaim.forEach {
            refreshToken = refreshToken.withClaim(it.userId, it.userIdValue)
        }
        return refreshToken.sign(Algorithm.HMAC256(refreshTokenConfig.secret))

//        return JWT.create()
//            .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenConfig.expiresIn))
//            .withClaim("refreshToken", UUID.randomUUID().toString())
//            .sign(Algorithm.HMAC256(refreshTokenConfig.secret))
    }

    override fun decodeRefreshToken(refreshToken: String): TokenPayload {
        val parts = refreshToken.split(".")
        return try {
            val charset = Charsets.UTF_8
            val payload = String(Base64.getUrlDecoder().decode(parts[1]), charset)

            val jsonObject = Json.parseToJsonElement(payload).jsonObject
            val userId = jsonObject["userId"]?.jsonPrimitive?.int
            val exp = jsonObject["exp"]?.jsonPrimitive?.long

            TokenPayload(userId = userId, expiresIn = exp)
        } catch (e: Exception) {
            TokenPayload(userId = null, expiresIn = null)
        }
    }
}