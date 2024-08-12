package ru.marinovdev.data.code

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.marinovdev.domain.repository.CodeDataSourceRepository

class CodeDataSourceRepositoryImpl(private val codeEntity: CodeEntity) : CodeDataSourceRepository {
    override fun insertCodeToDb(codeDTO: CodeDTO, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        try {
            transaction {
                codeEntity.insert {
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

    override fun fetchCode(receiveUserId: Int, onSuccess: (CodeDTO) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            transaction {
                val codeModel = codeEntity.select { codeEntity.userId.eq(receiveUserId) }.single()
                val codeDTO = CodeDTO(
                    userId = receiveUserId,
                    timeOfCreation = codeModel[codeEntity.timeOfCreation],
                    hashCodeSalt = codeModel[codeEntity.code],
                    salt = codeModel[codeEntity.salt]
                )
                onSuccess(codeDTO)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun deleteCode(receiveUserId: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        try {
            codeEntity.deleteWhere { userId eq receiveUserId }
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}