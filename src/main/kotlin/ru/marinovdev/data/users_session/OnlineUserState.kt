package ru.marinovdev.data.users_session

import kotlinx.serialization.Serializable

@Serializable
data class OnlineUserState(
    val userPhone: String,
    val onlineOrOffline: String
)