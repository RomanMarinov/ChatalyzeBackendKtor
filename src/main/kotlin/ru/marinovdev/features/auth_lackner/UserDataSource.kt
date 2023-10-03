package ru.marinovdev.features.auth_lackner

interface UserDataSource {
    suspend fun getUserByUsername(username: String) : User?
    suspend fun insertUser(user: User) : Boolean
}