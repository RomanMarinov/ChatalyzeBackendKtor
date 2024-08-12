package ru.marinovdev.data.users_session

import org.jetbrains.exposed.sql.Table

object UserSocketConnectionEntity : Table("user_session") {
    val id = UserSocketConnectionEntity.integer("id")
    val userPhone = UserSocketConnectionEntity.varchar("user_phone", 20)
    val onlineOrOffline = UserSocketConnectionEntity.varchar("online_or_offline", 30)
    val companionPhone = UserSocketConnectionEntity.varchar("companion_phone", 20)
}