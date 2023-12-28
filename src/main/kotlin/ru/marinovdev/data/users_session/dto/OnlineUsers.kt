package ru.marinovdev.data.users_session.dto

import kotlinx.serialization.Serializable
import ru.marinovdev.data.users_session.OnlineUserState


@Serializable
data class MessageWrapper(
    val type: String,
    //@SerialName("payload")
    val payloadJson: String
)


@Serializable
data class TestClassText(
    val text: String
)

@Serializable
data class OnlineUsers(
    val onlineUsers: List<OnlineUserState>
)
