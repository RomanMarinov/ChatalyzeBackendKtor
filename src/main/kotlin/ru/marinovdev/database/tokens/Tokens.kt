package ru.marinovdev.database.tokens

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Tokens : Table("token") {
    private val userId = Tokens.integer("user_id")
    private val refreshToken = Tokens.varchar("refresh_token", 200)

    fun insertRefreshToken(
        tokenDTO: TokenDTO,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                Tokens.insert {
                    it[userId] = tokenDTO.userId
                    it[refreshToken] = tokenDTO.refreshToken
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun fetchRefreshTokenByUserId(
        userId: Int,
        onSuccess: (String?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val tokenModel = Tokens.select { Tokens.userId.eq(userId) }.single()
                val refreshToken = tokenModel[refreshToken]
                onSuccess(refreshToken)
            }
        } catch (e: NoSuchElementException) {
            onSuccess(null)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun checkRefreshTokenToDb(
        refreshToken: String,
        onSuccess: (String?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val tokenModel = Tokens.select { Tokens.refreshToken.eq(refreshToken) }.single()
                val token = tokenModel[Tokens.refreshToken]
                onSuccess(token)
            }
        } catch (e: NoSuchElementException) {
            onSuccess(null)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun deleteRefreshTokenToDb(
        refreshToken: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                Tokens.deleteWhere { Tokens.refreshToken eq refreshToken }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun deleteRefreshTokenByUserId(
        userId: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        return try {
            transaction {
                Tokens.deleteWhere { Tokens.userId eq userId }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}