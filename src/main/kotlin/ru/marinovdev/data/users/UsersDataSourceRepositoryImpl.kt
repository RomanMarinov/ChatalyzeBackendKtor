package ru.marinovdev.data.users

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.marinovdev.data.users.dto.UserDTO
import ru.marinovdev.domain.repository.UsersDataSourceRepository

class UsersDataSourceRepositoryImpl(private val userEntity: UsersEntity) : UsersDataSourceRepository {
    override fun insertUser(userDTO: UserDTO, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        try {
            transaction {
                userEntity.insert {
                    it[userEntity.email] = userDTO.email
                    it[userEntity.password] = userDTO.password
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun findUserIdByEmail(emailFromDb: String, onSuccess: (Int?) -> Unit, onFailure: (Exception) -> Unit) {
        return try {
            transaction {
                val userModel = userEntity.select { userEntity.email.eq(emailFromDb) }.single()
                val userId = userModel[userEntity.userId]
                onSuccess(userId)
            }
        } catch (e: NoSuchElementException) {
            onSuccess(null)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun fetchUserByEmail(
        receivedEmail: String,
        onSuccess: (UserDTO?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        return try {
            transaction {
                val userModel = userEntity.select { userEntity.email.eq(receivedEmail) }.single()
                val userDTO = UserDTO(
                    email = userModel[userEntity.email],
                    password = userModel[userEntity.password]
                )
                onSuccess(userDTO)
            }
        } catch (e: NoSuchElementException) {
            onSuccess(null)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun fetchUserByUserId(id: Int, onSuccess: (UserDTO?) -> Unit, onFailure: (Exception) -> Unit) {
        return try {
            transaction {
                val userModel = userEntity.select { userEntity.userId.eq(id) }.single()
                val userDTO = UserDTO(
                    email = userModel[userEntity.email],
                    password = userModel[userEntity.password],
                )
                onSuccess(userDTO)
            }
        } catch (e: NoSuchElementException) {
            onSuccess(null)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun deleteUserByUserId(id: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        return try {
            transaction {
                userEntity.deleteWhere { userEntity.userId eq id }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun checkEmailExists(emailFromDb: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        return try {
            transaction {
                val emailExists = userEntity.select { userEntity.email.eq(emailFromDb) }.any()
                onSuccess(emailExists)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun updatePasswordHex(
        emailReceived: String,
        passwordGenerated: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                userEntity.update({ userEntity.email eq emailReceived }) {
                    it[userEntity.password] = passwordGenerated
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}