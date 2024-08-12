package ru.marinovdev.data.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.marinovdev.data.firebase.model.FirebaseCommand
import ru.marinovdev.data.firebase.model.FirebaseCommandSend
import ru.marinovdev.data.firebase.model.UserFirebase
import ru.marinovdev.domain.repository.UserFirebaseRepository

class UserFirebaseRepositoryImpl(private val firebaseRegisterEntity: FirebaseRegisterEntity) : UserFirebaseRepository {
    override fun checkUserFirebaseByPhone(
        userFirebase: UserFirebase,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val userFirebaseExists = firebaseRegisterEntity.select {
                    firebaseRegisterEntity.registerSenderPhone.eq(userFirebase.registerSenderPhone)
                }.any()
                onSuccess(userFirebaseExists)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun insertUserFirebaseToDb(
        userFirebase: UserFirebase,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                firebaseRegisterEntity.insert {
                    it[firebaseRegisterEntity.firebaseToken] = userFirebase.firebaseToken
                    it[firebaseRegisterEntity.registerSenderPhone] = userFirebase.registerSenderPhone
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun updateUserFirebaseToDb(
        userFirebase: UserFirebase,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                firebaseRegisterEntity.update({ firebaseRegisterEntity.registerSenderPhone eq userFirebase.registerSenderPhone }) {
                    it[firebaseRegisterEntity.firebaseToken] = userFirebase.firebaseToken
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun fetchUserFirebaseFromDb(
        firebaseCommand: FirebaseCommand,
        onSuccess: (UserFirebase) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val resultRow =
                    firebaseRegisterEntity.select { firebaseRegisterEntity.registerSenderPhone.eq(firebaseCommand.recipientPhone) }
                        .single()
                val userFirebase = UserFirebase(
                    firebaseToken = resultRow[firebaseRegisterEntity.firebaseToken],
                    registerSenderPhone = resultRow[firebaseRegisterEntity.registerSenderPhone]
                )
                onSuccess(userFirebase)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun fetchUserFirebaseFromDbForStream(
        firebaseCommand: FirebaseCommand,
        onSuccess: (UserFirebase) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val resultRow =
                    firebaseRegisterEntity.select { firebaseRegisterEntity.registerSenderPhone.eq(firebaseCommand.senderPhone) }
                        .single()
                val userFirebase = UserFirebase(
                    firebaseToken = resultRow[firebaseRegisterEntity.firebaseToken],
                    registerSenderPhone = resultRow[firebaseRegisterEntity.registerSenderPhone]
                )
                onSuccess(userFirebase)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override suspend fun sendCallCommandToFirebase(
        firebaseCommandSend: FirebaseCommandSend,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val dataMessage = Message.builder()
                .putData("firebaseToken", firebaseCommandSend.firebaseToken)
                .putData("topic", firebaseCommandSend.topic)
                .putData("senderPhone", firebaseCommandSend.senderPhone)
                .putData("textMessage", "textMessage")
                .putData("recipientPhone", firebaseCommandSend.recipientPhone)
                .putData("typeFirebaseCommand", firebaseCommandSend.typeFirebaseCommand)

            dataMessage.setToken(firebaseCommandSend.firebaseToken)
            dataMessage.setTopic(firebaseCommandSend.topic)
            try {
                FirebaseMessaging.getInstance().send(dataMessage.build())
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    override suspend fun sendPushMessageCommandToFirebase(
        firebaseCommandSend: FirebaseCommandSend,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val dataMessage = Message.builder()
                .putData("firebaseToken", firebaseCommandSend.firebaseToken)
                .putData("topic", firebaseCommandSend.topic)
                .putData("senderPhone", firebaseCommandSend.senderPhone)
                .putData("recipientPhone", firebaseCommandSend.recipientPhone)
                .putData("textMessage", firebaseCommandSend.textMessage)
                .putData("typeFirebaseCommand", firebaseCommandSend.typeFirebaseCommand)
            dataMessage.setToken(firebaseCommandSend.firebaseToken)
            dataMessage.setTopic(firebaseCommandSend.topic)
            try {
                FirebaseMessaging.getInstance().send(dataMessage.build())
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)

            }
        }
    }
}