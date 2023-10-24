package ru.marinovdev.database.codes

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Codes : Table("code") {
    private val userId = Codes.integer("user_id")
    private val timeOfCreation = Codes.long("time_of_creation")
    private val code = Codes.varchar("code", 200)
    private val salt = Codes.varchar("salt", 200)

    fun insertCodeToDb(
        codeDTO: CodeDTO,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                Codes.insert {
                    it[userId] = codeDTO.userId
                    it[timeOfCreation] = codeDTO.timeOfCreation
                    it[code] = codeDTO.hashCodeSalt
                    it[salt] = codeDTO.salt
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun fetchCode(
        receiveUserId: Int,
        onSuccess: (CodeDTO) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            transaction {
                val codeModel = Codes.select { userId.eq(receiveUserId) }.single()
                val codeDTO = CodeDTO(
                    userId = receiveUserId,
                    timeOfCreation = codeModel[timeOfCreation],
                    hashCodeSalt = codeModel[code],
                    salt = codeModel[salt]
                )
                onSuccess(codeDTO)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun deleteCode(
        receiveUserId: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            Codes.deleteWhere { userId eq receiveUserId }
            onSuccess()
        }
//        catch (e: NoSuchElementException) {
//            onSuccess(null)
//        }
        catch (e: Exception) {
            onFailure(e)
        }
    }
}