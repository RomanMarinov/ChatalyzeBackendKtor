package ru.marinovdev.domain.model.delete_profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteProfileReceiveRemote(
    @SerialName("refresh_token")
    val refreshToken: String
)
