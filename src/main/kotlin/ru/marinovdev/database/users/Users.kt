package ru.marinovdev.database.users

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

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

    //////////////////////////////
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
        }
        catch (e: Exception) {
            onFailure(e)
        }
    }


    ////////////////////////////

    fun fetchUser(
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
        }
        catch (e: Exception) {
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


}



/////////////////////////////////////////
//object Users : Table("users") {
//
//    private val email = Users.varchar("email", 30)
//    private val password = Users.varchar("password", 100)
//    private val salt = Users.varchar("salt", 100)
//
//    fun insertUser(
//        userDTO: UserDTO,
//        onSuccess: () -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        try {
//            transaction {
//                Users.insert {
//                    it[email] = userDTO.email
//                    it[password] = userDTO.password
//                    it[salt] = userDTO.salt
//                }
//                onSuccess()
//            }
//        } catch (e: Exception) {
//            onFailure(e)
//        }
//    }
//
//    fun fetchUser(
//        receivedEmail: String,
//        onSuccess: (UserDTO?) -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        return try {
//            transaction {
//                val userModel = Users.select { email.eq(receivedEmail) }.single()
//                val userDTO = UserDTO(
//                    email = userModel[email],
//                    password = userModel[password],
//                    salt = userModel[salt]
//                )
//                onSuccess(userDTO)
//            }
//        } catch (e: NoSuchElementException) {
//            onSuccess(null)
//        }
//        catch (e: Exception) {
//            onFailure(e)
//        }
//    }
//
//    fun checkEmailExists(
//        emailFromDb: String,
//        onSuccess: (Boolean) -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        return try {
//            transaction {
//                val emailExists = Users.select { email.eq(emailFromDb) }.any()
//                onSuccess(emailExists)
//            }
//        } catch (e: Exception) {
//            onFailure(e)
//        }
//    }
//}