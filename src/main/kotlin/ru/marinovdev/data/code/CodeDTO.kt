package ru.marinovdev.data.code

data class CodeDTO(
    val userId: Int,
    val timeOfCreation: Long,
    val hashCodeSalt: String,
    val salt: String
)
