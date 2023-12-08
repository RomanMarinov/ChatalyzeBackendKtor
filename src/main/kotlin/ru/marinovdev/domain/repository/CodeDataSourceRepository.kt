package ru.marinovdev.domain.repository

import ru.marinovdev.data.code.CodeDTO

interface CodeDataSourceRepository {
    fun insertCodeToDb(codeDTO: CodeDTO, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun fetchCode(receiveUserId: Int, onSuccess: (CodeDTO) -> Unit, onFailure: (Exception) -> Unit)
    fun deleteCode(receiveUserId: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}