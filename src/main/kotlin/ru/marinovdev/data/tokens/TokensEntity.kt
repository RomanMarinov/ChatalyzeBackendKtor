package ru.marinovdev.data.tokens

import org.jetbrains.exposed.sql.Table

object TokensEntity : Table("token") {
    val userId = TokensEntity.integer("user_id")
    val refreshToken = TokensEntity.varchar("refresh_token", 200)
}