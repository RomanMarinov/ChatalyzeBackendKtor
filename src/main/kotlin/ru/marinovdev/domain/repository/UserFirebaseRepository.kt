package ru.marinovdev.domain.repository

import ru.marinovdev.data.firebase.model.FirebaseCommand
import ru.marinovdev.data.firebase.model.FirebaseCommandSend
import ru.marinovdev.data.firebase.model.UserFirebase

interface UserFirebaseRepository {
    fun checkUserFirebaseByPhone(userFirebase: UserFirebase, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)
    fun insertUserFirebaseToDb(userFirebase: UserFirebase, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun updateUserFirebaseToDb(userFirebase: UserFirebase, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun fetchUserFirebaseFromDb(firebaseCommand: FirebaseCommand, onSuccess: (UserFirebase) -> Unit, onFailure: (Exception) -> Unit)
    suspend fun sendCallCommandToFirebase(firebaseCommandSend: FirebaseCommandSend, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    suspend fun sendPushMessageCommandToFirebase(firebaseCommandSend: FirebaseCommandSend, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun fetchUserFirebaseFromDbForStream(firebaseCommand: FirebaseCommand, onSuccess: (UserFirebase) -> Unit, onFailure: (Exception) -> Unit)

}