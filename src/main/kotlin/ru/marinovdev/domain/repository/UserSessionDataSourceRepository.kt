package ru.marinovdev.domain.repository

import ru.marinovdev.data.users_session.OnlineUserState
import ru.marinovdev.data.users_session.UserSocketConnection

interface UserSessionDataSourceRepository {
    fun insertUserSession(
        userPhone: String,
        onlineOrOffline: String,
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
        onlineOrOffline: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun updateUserSessionCompanion(
        senderPone: String,
        companionPhone: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun getUserSessionCompanion(
        senderPhone: String,
        companionPhone: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun getListOnlineOrOffline(
        listRecipient: List<String>,
        onSuccess: (List<OnlineUserState>) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun getUserSession(
        userPhone: String,
        onSuccess: (UserSocketConnection) -> Unit,
        onFailure: (Exception) -> Unit
    )
}