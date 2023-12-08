package ru.marinovdev.domain.repository

import ru.marinovdev.data.users_session.OnlineOrDate
import ru.marinovdev.data.users_session.UserSocketConnection

interface UserSessionDataSourceRepository {
    fun insertUserSession(
        userPhone: String,
        onlineOrDate: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun checkUserSessionExists(
        userPhone: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun updateUserSession(
        userPhone: String,
        onlineOrDate: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun getListOnlineOrDate(
        listRecipient: List<String>,
        onSuccess: (List<OnlineOrDate>) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun getUserSession(
        userPhone: String,
        onSuccess: (UserSocketConnection) -> Unit,
        onFailure: (Exception) -> Unit
    )
}