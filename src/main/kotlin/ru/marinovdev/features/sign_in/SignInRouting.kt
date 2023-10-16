package ru.marinovdev.features.sign_in

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.marinovdev.features.auth_lackner.security.hashing.SHA256HashingService
import ru.marinovdev.features.auth_lackner.security.token.AccessTokenConfig
import ru.marinovdev.features.auth_lackner.security.token.JwtTokenService
import ru.marinovdev.features.auth_lackner.security.token.RefreshTokenConfig

fun Application.configureSignInRouting(
    hashingService: SHA256HashingService,
    jwtTokenService: JwtTokenService,
    accessTokenConfig: AccessTokenConfig,
    refreshTokenConfig: RefreshTokenConfig
) {
    routing {
        post("/signin") {
            val signInController = SignInController(
                call = call,
                hashingService = hashingService,
                tokenService = jwtTokenService,
                accessTokenConfig = accessTokenConfig,
                refreshTokenConfig = refreshTokenConfig
            )
            signInController.performSignIn()
        }
    }
}