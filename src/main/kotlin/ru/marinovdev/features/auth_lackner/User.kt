package ru.marinovdev.features.auth_lackner

data class User(
    val email: String,
    val password: String,
    val salt: String,
   // val id: Int // уникальный id каждого пользователя
)
