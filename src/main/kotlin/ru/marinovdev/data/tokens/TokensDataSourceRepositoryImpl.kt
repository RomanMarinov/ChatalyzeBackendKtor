package ru.marinovdev.data.tokens

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.marinovdev.data.tokens.dto.TokenDTO
import ru.marinovdev.domain.repository.TokensDataSourceRepository
import ru.marinovdev.features.jwt_token.JwtConfig
import ru.marinovdev.utils.StringResource

class TokensDataSourceRepositoryImpl (private val tokensEntity: TokensEntity) : TokensDataSourceRepository {
    override fun insertRefreshToken(tokenDTO: TokenDTO, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        try {
            transaction {
                tokensEntity.insert {
                    it[userId] = tokenDTO.userId
                    it[refreshToken] = tokenDTO.refreshToken
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun fetchRefreshTokenByUserId(userId: Int, onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            transaction {
                val tokenModel = tokensEntity.select { tokensEntity.userId.eq(userId) }.single()
                val refreshToken = tokenModel[tokensEntity.refreshToken]
                onSuccess(refreshToken)
            }
        } catch (e: NoSuchElementException) {
            onSuccess(null)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun checkRefreshTokenToDb(
        refreshToken: String,
        onSuccess: (String?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val tokenModel = tokensEntity.select { tokensEntity.refreshToken.eq(refreshToken) }.single()
                val token = tokenModel[tokensEntity.refreshToken]
                onSuccess(token)
            }
        } catch (e: NoSuchElementException) {
            onSuccess(null)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun checkTokenVerification(token: String, onVerifiedResult: (String) -> Unit, ) {
        try {
            val jwtVerifier: JWTVerifier = JWT.require(Algorithm.HMAC256(JwtConfig.getSecret())).build()
            val res: DecodedJWT = jwtVerifier.verify(token)
            val signature: String = res.signature
            onVerifiedResult(StringResource.TOKEN_VERIFIED)
        } catch (e: SignatureVerificationException) {
            println(":::::::::::::::: try catch checkTokenVerification e=" + e)
            onVerifiedResult(StringResource.TOKEN_NOT_VERIFIED)
        } catch (e: TokenExpiredException) {
            println(":::::::::::::::: try catch TokenExpiredException e=" + e)
            onVerifiedResult(StringResource.TOKEN_EXPIRED)
        }
    }

    override fun deleteRefreshTokenToDb(refreshToken: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        try {
            transaction {
                tokensEntity.deleteWhere { tokensEntity.refreshToken eq refreshToken }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun deleteRefreshTokenByUserId(userId: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        return try {
            transaction {
                tokensEntity.deleteWhere { tokensEntity.userId eq userId }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}