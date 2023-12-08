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
                val userSessionExists = userSocketConnectionEntity.select { userSocketConnectionEntity.userPhone.eq(userPhone) }.any()
                onSuccess(userSessionExists)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun updateUserSession(
        userPhone: String,
        onlineOrDate: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                userSocketConnectionEntity.update({ userSocketConnectionEntity.userPhone eq userPhone }) {
                    it[userSocketConnectionEntity.onlineOrDate] = onlineOrDate
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun getListOnlineOrDate(
        listRecipient: List<String>,
        onSuccess: (List<OnlineOrDate>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val listOnlineOrDate = listRecipient.mapNotNull { recipientPhone ->
                    val row =
                        userSocketConnectionEntity.select { userSocketConnectionEntity.userPhone eq recipientPhone }.singleOrNull()
                    row?.let {
                        OnlineOrDate(
                            userPhone = row[userSocketConnectionEntity.userPhone],
                            onlineOrDate = row[userSocketConnectionEntity.onlineOrDate]
                        )
                    }
                }
                onSuccess(listOnlineOrDate)
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
                val userSessionRow = userSocketConnectionEntity.select { userSocketConnectionEntity.userPhone eq userPhone }.single()
                val userSocketConnection = UserSocketConnection(
                    userPhone = userSessionRow[userSocketConnectionEntity.userPhone],
                    onlineOrDate = userSessionRow[userSocketConnectionEntity.onlineOrDate]
                )
                onSuccess(userSocketConnection)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun insertUserSession(
        userPhone: String,
        onlineOrDate: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                userSocketConnectionEntity.insert {
                    it[userSocketConnectionEntity.userPhone] = userPhone
                    it[userSocketConnectionEntity.onlineOrDate] = onlineOrDate
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}