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
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}