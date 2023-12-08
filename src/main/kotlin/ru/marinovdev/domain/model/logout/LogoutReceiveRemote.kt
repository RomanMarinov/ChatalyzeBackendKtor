package ru.marinovdev.domain.model.logout

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogoutReceiveRemote(
    @SerialName("refresh_token")
    val refresh_token: String
)
