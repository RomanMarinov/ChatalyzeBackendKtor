package ru.marinovdev.data.users_session

import kotlinx.serialization.Serializable

@Serializable
data class OnlineOrDate(
    val userPhone: String,
    //val userSocket: String,
    val onlineOrDate: String
)
