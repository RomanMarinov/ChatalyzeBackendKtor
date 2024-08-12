package ru.marinovdev.domain.repository

import ru.marinovdev.data.messages.model.Chat
import ru.marinovdev.data.messages.model.Message
import ru.marinovdev.data.messages.model.UserPairChat

interface MessageDataSourceRepository {
    suspend fun getAllMessages(
        userPairChat: UserPairChat,
        onSuccess: (List<Message>) -> Unit,
        onFailure: (Exception) -> Unit
    )

    suspend fun insertMessage(message: Message)
    suspend fun getChats(
        receivedSender: String,
        onSuccess: (List<Chat>) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun getListRecipientInDialogWithUserPhone(
        userPhone: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    )

    suspend fun getLastMessage(
        sender: String,
        recipient: String,
        onFailure: (Exception) -> Unit
    ) : Message?
}