package ru.marinovdev.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*


//fun Application.configureSecurity(config: TokenConfig) {
//    authentication {
//        jwt {
//            // realm` указывает на имя или идентификатор защищенной области.
//            // Значение для `realm` извлекается из конфигурации приложения
//            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
//            // `verifier`, который отвечает за проверку подлинности токена.
//            verifier(
//                JWT
//                    .require(Algorithm.HMAC256(config.secret))
//                    .withAudience(config.audience)
//                    .withIssuer(config.issuer)
//                    .build()
//            )
//            // validate` мы устанавливаем логику проверки токена.
//            // проверяем, содержится ли указанная аудитория `config.audience` в полезной нагрузке токена.
//            // Если проверка успешна, то мы создаем `JWTPrincipal` на основе полезной нагрузки токена
//            // и возвращаем его. В противном случае возвращаем `null`.
//            validate { credential ->
//                if (credential.payload.audience.contains(config.audience)) {
//                    JWTPrincipal(credential.payload)
//                } else null
//            }
//        }
//    }
//
//}