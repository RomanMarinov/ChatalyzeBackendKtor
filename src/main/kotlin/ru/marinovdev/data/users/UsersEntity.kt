package ru.marinovdev.data.users

import org.jetbrains.exposed.sql.Table

object UsersEntity : Table("users") {
     val userId = UsersEntity.integer("id")
     val email = UsersEntity.varchar("email", 30)
     val password = UsersEntity.varchar("password", 100)
     val salt = UsersEntity.varchar("salt", 100)
}