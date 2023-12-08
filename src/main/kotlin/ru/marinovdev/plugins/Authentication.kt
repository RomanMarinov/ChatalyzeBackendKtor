package ru.marinovdev.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import ru.marinovdev.features.jwt_token.JwtConfig

fun Application.configureAuthentication(hoconApplicationConfig: HoconApplicationConfig) {
    authentication {
        jwt {
//            val issuer = HoconApplicationConfig(ConfigFactory.load()).property("jwt.issuer").getString()
//            val audience = HoconApplicationConfig(ConfigFactory.load()).property("jwt.audience").getString()
//            val secret = HoconApplicationConfig(ConfigFactory.load()).property("jwt.secret").getString()

            realm = hoconApplicationConfig.property("jwt.realm_value").getString()
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


//fun Application.configureAuthentication(hoconApplicationConfig: HoconApplicationConfig) {
//    authentication {
//        jwt {
//            val issuer = HoconApplicationConfig(ConfigFactory.load()).property("jwt.issuer").getString()
//            val audience = HoconApplicationConfig(ConfigFactory.load()).property("jwt.audience").getString()
//            val secret = HoconApplicationConfig(ConfigFactory.load()).property("jwt.secret").getString()
//
//            realm = hoconApplicationConfig.property("jwt.realm_value").getString()
//            verifier(
//                JWT
//                    .require(Algorithm.HMAC256(secret))
//                    .withAudience(audience)
//                    .withIssuer(issuer)
//                    .build()
//            )
//
//            validate { credential ->
//                if (credential.payload.audience.contains(audience)) {
//                    JWTPrincipal(credential.payload)
//                } else null
//            }
//        }
//    }
//}