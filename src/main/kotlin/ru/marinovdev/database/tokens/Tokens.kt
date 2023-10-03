package ru.marinovdev.database.tokens

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Tokens : Table("token") {
    private val email = Tokens.varchar("email", 30)
    private val token = Tokens.varchar("token", 100)

    fun insertToken(
        tokenDTO: TokenDTO,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit) {
        try {
            transaction {
                Tokens.insert {
                    it[email] = tokenDTO.email
                    it[token] = tokenDTO.token
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun fetchToken(
        receivedEmail: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit) {
        try {
            transaction {
                val result: ResultRow? = Tokens.select { email eq receivedEmail }.singleOrNull()
                val tokenValue = result?.get(token)
                tokenValue?.let {
                    onSuccess(it)
                }
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}