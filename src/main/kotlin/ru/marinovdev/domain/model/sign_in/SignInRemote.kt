package ru.marinovdev.domain.model.sign_in

import kotlinx.serialization.Serializable

@Serializable
data class SignInReceiveRemote(
    val email: String,
    val password: String
)

@Serializable
data class SignInResponseRemote(
    val accessToken: String,
    val refreshToken: String
)