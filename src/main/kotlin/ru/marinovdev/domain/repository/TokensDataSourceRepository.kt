package ru.marinovdev.domain.repository

import ru.marinovdev.data.tokens.dto.TokenDTO

interface TokensDataSourceRepository {
    fun insertRefreshToken(tokenDTO: TokenDTO, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun fetchRefreshTokenByUserId(userId: Int, onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit)
    fun checkRefreshTokenToDb(refreshToken: String, onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit)
    fun deleteRefreshTokenToDb(refreshToken: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun deleteRefreshTokenByUserId(userId: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}