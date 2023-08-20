package ru.marinovdev.cache

import ru.marinovdev.features.register.RegisterReceiveRemote


data class TokenCache(
    val login: String,
    val token: String
)

object InMemoryCache {
    val userList: MutableList<RegisterReceiveRemote> = mutableListOf() // храним список регистрационных данных
    val token: MutableList<TokenCache> = mutableListOf()
}