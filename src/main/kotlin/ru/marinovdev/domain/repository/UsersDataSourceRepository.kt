package ru.marinovdev.domain.repository

import ru.marinovdev.data.users.dto.UserDTO

interface UsersDataSourceRepository {
    fun insertUser(userDTO: UserDTO, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun findUserIdByEmail(emailFromDb: String, onSuccess: (Int?) -> Unit, onFailure: (Exception) -> Unit)
    fun fetchUserByEmail(receivedEmail: String, onSuccess: (UserDTO?) -> Unit, onFailure: (Exception) -> Unit)
    fun fetchUserByUserId(id: Int, onSuccess: (UserDTO?) -> Unit, onFailure: (Exception) -> Unit)
    fun deleteUserByUserId(id: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun checkEmailExists(emailFromDb: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)
    fun updatePasswordHex(
        emailReceived: String,
        passwordGenerated: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )
}