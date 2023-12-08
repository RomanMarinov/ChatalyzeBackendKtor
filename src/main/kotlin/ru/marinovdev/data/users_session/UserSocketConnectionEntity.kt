package ru.marinovdev.data.users_session

import org.jetbrains.exposed.sql.Table

object UserSocketConnectionEntity : Table("user_session") {
    val id = UserSocketConnectionEntity.integer("id")
    val userPhone = UserSocketConnectionEntity.varchar("user_phone", 20)
    val onlineOrDate = UserSocketConnectionEntity.varchar("online_or_date", 30)
}