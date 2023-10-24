package ru.marinovdev.database.codes

data class CodeDTO(
    val userId: Int,
    val timeOfCreation: Long,
    val hashCodeSalt: String,
    val salt: String
)
