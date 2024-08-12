package ru.marinovdev.data.users_session

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.marinovdev.domain.repository.UserSessionDataSourceRepository

class UserSessionDataSourceRepositoryImpl(
    private val userSocketConnectionEntity: UserSocketConnectionEntity
) : UserSessionDataSourceRepository {

    override fun checkUserSessionExists(
        userPhone: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val userSessionExists =
                    userSocketConnectionEntity.select { userSocketConnectionEntity.userPhone.eq(userPhone) }.any()
                onSuccess(userSessionExists)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun updateUserSession(
        userPhone: String,
        onlineOrOffline: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                userSocketConnectionEntity.update({ userSocketConnectionEntity.userPhone eq userPhone }) {
                    it[userSocketConnectionEntity.onlineOrOffline] = onlineOrOffline
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun updateUserSessionCompanion(
        senderPone: String,
        companionPhone: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {

            transaction {
              println("::::::::::::: updateUserSessionCompanion senderPone=" + senderPone + " companionPhone=" + companionPhone)
                val alreadyExistsPhone = userSocketConnectionEntity.select { userSocketConnectionEntity.userPhone eq senderPone }.any()
                if (alreadyExistsPhone) {
                    userSocketConnectionEntity.update ({ userSocketConnectionEntity.userPhone eq senderPone }) {
                        it[userSocketConnectionEntity.companionPhone] = companionPhone
                    }
                    onSuccess()
                } else {
                    onFailure(Exception("Exception onFailure updateUserSessionCompanion userPhone is not already exists"))
                }
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun getUserSessionCompanion(
        senderPhone: String,
        companionPhone: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {

                println(":::::::::::::::::getUserSessionCompanion senderPhone=" + senderPhone + " companionPhone=" + companionPhone)
                println(":::::::::::::::::getUserSessionCompanion userSocketConnectionEntity.userPhone=" + userSocketConnectionEntity.userPhone + " companionPhone=" + companionPhone)


                val userPhone = userSocketConnectionEntity.select { userSocketConnectionEntity.userPhone eq companionPhone }.any()
                if (userPhone) {
                    val row = userSocketConnectionEntity.select { userSocketConnectionEntity.userPhone eq companionPhone }.singleOrNull()
                    row?.let {
                        val alreadyExistsCompanionPhone = userSocketConnectionEntity.select {
                            userSocketConnectionEntity.companionPhone eq senderPhone
                        }.any()
                        if (alreadyExistsCompanionPhone) {
                            onSuccess(true)
                        } else {
                            onSuccess(false)
                        }
                    }
                } else {
                    onFailure(Exception("SenderPone does not exist"))
                }
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun getListOnlineOrOffline(
        listRecipient: List<String>,
        onSuccess: (List<OnlineUserState>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val listOnlineUserState = listRecipient.mapNotNull { recipientPhone ->
                    val row =
                        userSocketConnectionEntity.select { userSocketConnectionEntity.userPhone eq recipientPhone }
                            .singleOrNull()
                    row?.let {
                        OnlineUserState(
                            userPhone = row[userSocketConnectionEntity.userPhone],
                            onlineOrOffline = row[userSocketConnectionEntity.onlineOrOffline]
                        )
                    }
                }
                onSuccess(listOnlineUserState)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun getUserSession(
        userPhone: String,
        onSuccess: (UserSocketConnection) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val userSessionRow =
                    userSocketConnectionEntity.select { userSocketConnectionEntity.userPhone eq userPhone }.single()
                val userSocketConnection = UserSocketConnection(
                    userPhone = userSessionRow[userSocketConnectionEntity.userPhone],
                    onlineOrOffline = userSessionRow[userSocketConnectionEntity.onlineOrOffline]
                )
                onSuccess(userSocketConnection)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun insertUserSession(
        userPhone: String,
        onlineOrOffline: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                userSocketConnectionEntity.insert {
                    it[userSocketConnectionEntity.userPhone] = userPhone
                    it[userSocketConnectionEntity.onlineOrOffline] = onlineOrOffline
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}