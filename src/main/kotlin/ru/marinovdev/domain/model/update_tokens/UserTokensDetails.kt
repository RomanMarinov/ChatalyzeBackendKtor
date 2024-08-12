package ru.marinovdev.domain.model.update_tokens

import kotlinx.serialization.Serializable

@Serializable
data class UserTokensDetails(
    val accessToken: String,
    val refreshToken: String,
    val userId: Int
)

