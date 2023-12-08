package ru.marinovdev.data.messages

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.`java-time`.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import ru.marinovdev.data.messages.model.Message

object MessageEntity : Table("message") {
     val id = MessageEntity.integer("id")
     val sender = MessageEntity.varchar("sender", 20)
     val recipient = MessageEntity.varchar("recipient", 20)
     val textMessage = MessageEntity.text("text_message")
     val createdAt = MessageEntity.timestamp("created_at")

//    fun fetchChat(
//        receivedSender: String,
//        onSuccess: (List<Chat>) -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        try {
//            transaction {
//                val results = mutableMapOf<String, Chat>()
//
//                MessageEntity
//                    .select { (MessageEntity.sender eq receivedSender) or (MessageEntity.recipient eq receivedSender) }
//                    .orderBy(MessageEntity.createdAt to SortOrder.DESC)
//                    .forEach {
//                        val sender = it[MessageEntity.sender]
//                        val recipient = it[MessageEntity.recipient]
//                        val chatDTO = Chat(
//                            sender = sender,
//                            recipient = recipient,
//                            text_message = it[MessageEntity.textMessage],
//                            created_at = it[MessageEntity.createdAt].toString()
//                        )
//
//                        // Если это сообщение от receivedSender
//                        if (sender == receivedSender) {
//                            // Если ранее не было сообщений с этим получателем или если это сообщение более новое
//                            if (recipient !in results || chatDTO.created_at > results[recipient]?.created_at.toString()) {
//                                results[recipient] = chatDTO
//                            }
//                        }
//
//                        // Если это сообщение для receivedSender
//                        else if (recipient == receivedSender) {
//                            // Если ранее не было сообщений от этого отправителя или если это сообщение более новое
//                            if (sender !in results || chatDTO.created_at > results[sender]?.created_at.toString()) {
//                                results[sender] = chatDTO
//                            }
//                        }
//                    }
//                onSuccess(results.values.toList())
//            }
//        } catch (e: Exception) {
//            onFailure(e)
//        }
//    }

    fun insertMessage(
        message: Message,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                MessageEntity.insert {
                    it[sender] = message.sender.replace("\"", "")
                    it[recipient] = message.recipient.replace("\"", "")
                    it[textMessage] = message.textMessage
                   // it[createdAt] = createdAt
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
//
//    fun fetchMessagesByPairUsers(
//        userPairChat: UserPairChat,
//        onSuccess: (List<Message>) -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        try {
//            transaction {
//                val messages = MessageEntity
//                    .select {
//                        (((MessageEntity.sender eq userPairChat.sender) and (MessageEntity.recipient eq userPairChat.recipient)) or
//                                ((MessageEntity.sender eq userPairChat.recipient) and (MessageEntity.recipient eq userPairChat.sender)))
//                    }
//                    .map {
//                        Message(
//                            sender = it[MessageEntity.sender],
//                            recipient = it[MessageEntity.recipient],
//                            textMessage = it[MessageEntity.textMessage],
//                            createdAt = it[MessageEntity.createdAt].toString()
//                        )
//                    }
//                onSuccess(messages)
//            }
//        } catch (e: Exception) {
//            onFailure(e)
//        }
//    }
}