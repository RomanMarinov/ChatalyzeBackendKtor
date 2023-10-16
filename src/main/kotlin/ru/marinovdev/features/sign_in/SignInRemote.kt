package ru.marinovdev.features.sign_in

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

//@Serializable
//data class LoginReceiveRemote(
//    val login: String,
//    val password: String
//)
//
//@Serializable
//data class LoginResponseRemote(
//    val token: String
//)
