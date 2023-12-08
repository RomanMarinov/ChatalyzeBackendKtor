package ru.marinovdev.data.code

import org.jetbrains.exposed.sql.Table

object CodeEntity : Table("code") {
    val userId = CodeEntity.integer("user_id")
    val timeOfCreation = CodeEntity.long("time_of_creation")
    val code = CodeEntity.varchar("code", 200)
    val salt = CodeEntity.varchar("salt", 200)
}