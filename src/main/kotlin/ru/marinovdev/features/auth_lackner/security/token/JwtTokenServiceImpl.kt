package ru.marinovdev.features.auth_lackner.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.serialization.json.*
import ru.marinovdev.features.jwt_token.JwtConfig
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
        return accessToken.sign(Algorithm.HMAC256(JwtConfig.getSecret()))
    }

    override fun generateRefreshToken(refreshTokenConfig: RefreshTokenConfig, vararg tokenClaim: TokenClaim): String {
        var refreshToken = JWT.create()
            .withAudience(refreshTokenConfig.audience)
            .withIssuer(refreshTokenConfig.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenConfig.expiresIn * 1000))
        tokenClaim.forEach {
            refreshToken = refreshToken.withClaim(it.userId, it.userIdValue)
        }
        return refreshToken.sign(Algorithm.HMAC256(JwtConfig.getSecret()))
    }

    override fun decodeToken(token: String): TokenPayload {
        val parts = token.split(".")
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

    override fun checkCorrectSecret(accessToken: String): Boolean {
        val algorithm: Algorithm = Algorithm.HMAC256(JwtConfig.getSecret())
        val decodedToken: DecodedJWT = JWT.require(algorithm).build().verify(accessToken)
        val accessTokenSecret: String = decodedToken.subject
        return true
    }
}