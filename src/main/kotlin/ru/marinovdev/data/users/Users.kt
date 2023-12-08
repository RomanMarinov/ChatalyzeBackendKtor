package ru.marinovdev.data.users

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.marinovdev.data.users.dto.UserDTO

object Users : Table("users") {
    private val userId = Users.integer("id")
    private val email = Users.varchar("email", 30)
    private val password = Users.varchar("password", 100)
    private val salt = Users.varchar("salt", 100)

    fun insertUser(
        userDTO: UserDTO,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                Users.insert {
                    it[email] = userDTO.email
                    it[password] = userDTO.password
                    it[salt] = userDTO.salt
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun findUserIdByEmail(
        emailFromDb: String,
        onSuccess: (Int?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        return try {
            transaction {
                val userModel = Users.select { email.eq(emailFromDb) }.single()
                val userId = userModel[userId]

                onSuccess(userId)
            }
        } catch (e: NoSuchElementException) {
            onSuccess(null)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun fetchUserByEmail(
        receivedEmail: String,
        onSuccess: (UserDTO?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        return try {
            transaction {
                val userModel = Users.select { email.eq(receivedEmail) }.single()
                val userDTO = UserDTO(
                    email = userModel[email],
                    password = userModel[password],
                    salt = userModel[salt]
                )
                onSuccess(userDTO)
            }
        } catch (e: NoSuchElementException) {
            onSuccess(null)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun fetchUserByUserId(
        id: Int,
        onSuccess: (UserDTO?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        return try {
            transaction {
                val userModel = Users.select { userId.eq(id) }.single()
                val userDTO = UserDTO(
                    email = userModel[email],
                    password = userModel[password],
                    salt = userModel[salt]
                )
                onSuccess(userDTO)
            }
        } catch (e: NoSuchElementException) {
            onSuccess(null)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun deleteUserByUserId(
        id: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        return try {
            transaction {
                Users.deleteWhere { userId eq id }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun checkEmailExists(
        emailFromDb: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        return try {
            transaction {
                val emailExists = Users.select { email.eq(emailFromDb) }.any()
                onSuccess(emailExists)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun updatePasswordAndSalt(
        emailReceived: String,
        passwordGenerated: String,
        saltGenerated: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                Users.update({ email eq emailReceived }) {
                    it[password] = passwordGenerated
                    it[salt] = saltGenerated
                }
            }
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}