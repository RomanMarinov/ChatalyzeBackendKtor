package ru.marinovdev.data.messages

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.marinovdev.data.messages.model.Chat
import ru.marinovdev.data.messages.model.Message
import ru.marinovdev.data.messages.model.UserPairChat
import ru.marinovdev.domain.repository.MessageDataSourceRepository

class MessageDataSourceRepositoryImpl(
    private val messageEntity: MessageEntity
) : MessageDataSourceRepository {

    override suspend fun getAllMessages(
        userPairChat: UserPairChat,
        onSuccess: (List<Message>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val messages = MessageEntity
                    .select {
                        (((MessageEntity.sender eq userPairChat.sender) and (MessageEntity.recipient eq userPairChat.recipient)) or
                                ((MessageEntity.sender eq userPairChat.recipient) and (MessageEntity.recipient eq userPairChat.sender)))
                    }
                    .map {
                        Message(
                            sender = it[MessageEntity.sender],
                            recipient = it[MessageEntity.recipient],
                            textMessage = it[MessageEntity.textMessage],
                            createdAt = it[MessageEntity.createdAt].toString()
                        )
                    }
                onSuccess(messages)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override suspend fun insertMessage(message: Message) {
        println(":::::::::::::::MessageDataSourceImpl insertMessage message=" + message)
        MessageEntity.insertMessage(
            message = message,
            onSuccess = {
                println(":::::::::::::::MessageDataSourceImpl insertMessage onSuccess")
            },
            onFailure = {
                println(":::::::::::::::MessageDataSourceImpl insertMessage onFailure")
            }
        )
    }

    override suspend fun getChats(
        receivedSender: String,
        onSuccess: (List<Chat>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val results = mutableMapOf<String, Chat>()

                MessageEntity
                    .select { (MessageEntity.sender eq receivedSender) or (MessageEntity.recipient eq receivedSender) }
                    .orderBy(MessageEntity.createdAt to SortOrder.DESC)
                    .forEach {
                        val sender = it[MessageEntity.sender]
                        val recipient = it[MessageEntity.recipient]
                        val chatDTO = Chat(
                            sender = sender,
                            recipient = recipient,
                            text_message = it[MessageEntity.textMessage],
                            created_at = it[MessageEntity.createdAt].toString()
                        )

                        if (sender == receivedSender) {
                            // Если ранее не было сообщений с этим получателем или если это сообщение более новое
                            if (recipient !in results || chatDTO.created_at > results[recipient]?.created_at.toString()) {
                                results[recipient] = chatDTO
                            }
                        } else if (recipient == receivedSender) {
                            // Если ранее не было сообщений от этого отправителя или если это сообщение более новое
                            if (sender !in results || chatDTO.created_at > results[sender]?.created_at.toString()) {
                                results[sender] = chatDTO
                            }
                        }
                    }
                onSuccess(results.values.toList())
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun getListRecipientInDialogWithUserPhone(
        userPhone: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val listPairDialogSender = messageEntity
                    .slice(messageEntity.recipient) // выбираем область
                    .select { messageEntity.sender eq userPhone } // выбирает из этой области условие, где...
                    .map { it[messageEntity.recipient] } // преобразуем в список

                val listPairDialogRecipient = messageEntity
                    .slice(messageEntity.sender) // выбираем область
                    .select { messageEntity.recipient eq userPhone } // выбирает из этой области условие, где...
                    .map { it[messageEntity.sender] } // преобразуем в список

                val listPairDialog = (listPairDialogSender + listPairDialogRecipient)
                    .filter { it != userPhone } // исключаем номер userPhone
                    .distinct() // делаем каждое значение уникальным, удаляя повторы
                onSuccess(listPairDialog)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override suspend fun getLastMessage(
        sender: String,
        recipient: String,
        onFailure: (Exception) -> Unit
    ): Message? {
        return try {
            transaction {
                val lastMessage = messageEntity
                    .slice(
                        messageEntity.sender,
                        messageEntity.recipient,
                        messageEntity.textMessage,
                        messageEntity.createdAt
                    )
                    .select { (messageEntity.sender eq sender) and (messageEntity.recipient eq recipient) }
                    .orderBy(messageEntity.createdAt to SortOrder.DESC)
                    .limit(1)
                    .map { resultRow ->
                        Message(
                            sender = resultRow[messageEntity.sender],
                            recipient = resultRow[messageEntity.recipient],
                            textMessage = resultRow[messageEntity.textMessage],
                            createdAt = resultRow[messageEntity.createdAt].toString()
                        )
                    }.firstOrNull()

                println("::::::::::::::::::::::lastMessage=" + lastMessage)
                lastMessage
            }
        } catch (e: Exception) {
            onFailure(e)
            null
        }
    }
}