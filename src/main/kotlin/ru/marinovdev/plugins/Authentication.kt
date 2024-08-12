package ru.marinovdev.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import ru.marinovdev.features.jwt_token.JwtConfig

fun Application.configureAuthentication() {
    install(Authentication) {
        jwt("jwt") {
            realm = JwtConfig.getRealm()
            verifier(
                JWT
                    .require(Algorithm.HMAC256(JwtConfig.getSecret()))
                    .withAudience(JwtConfig.getAudience())
                    .withIssuer(JwtConfig.getIssuer())
                    .build()
            )

            validate { credential ->
                if (credential.payload.audience.contains(JwtConfig.getAudience())) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}