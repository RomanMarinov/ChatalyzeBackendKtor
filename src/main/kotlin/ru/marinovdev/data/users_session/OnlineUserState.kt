package ru.marinovdev.data.users_session

import kotlinx.serialization.Serializable

@Serializable
data class OnlineUserState(
    val userPhone: String,
    //val userSocket: String,
    val onlineOrDate: String
)

@Serializable
data class TestClassText(
    val text: String
)
