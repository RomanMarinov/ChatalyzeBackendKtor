package ru.marinovdev.features.delete_profile

import io.ktor.server.application.*
import io.ktor.server.request.*
import ru.marinovdev.features.auth_lackner.security.token.JwtTokenService
import ru.marinovdev.features.auth_lackner.security.token.TokenPayload

class DeleteProfileController(
    private val call: ApplicationCall,
    private val jwtTokenService: JwtTokenService
) {
    suspend fun delete() {
        val receive = call.receive<DeleteProfileReceiveRemote>()
        println("::::::::::::::::::DeleteProfileController delete receive refreshToken=" + receive.refreshToken)

        val tokenPayload: TokenPayload = jwtTokenService.decodeRefreshToken(receive.refreshToken)

        val userId = tokenPayload.userId
        val expiresIn = tokenPayload.expiresIn

        println("::::::::::::::::::DeleteProfileController userId=" + userId)
        println("::::::::::::::::::DeleteProfileController expiresIn=" + expiresIn)

        //




    }
}