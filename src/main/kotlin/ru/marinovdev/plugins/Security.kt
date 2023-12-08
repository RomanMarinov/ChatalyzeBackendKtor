package ru.marinovdev.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import ru.marinovdev.data.socket_connection.ChatSession

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<ChatSession>("SESSION")
    }

    println(":::::::::::::::::::configureSecurity execute")
    intercept(ApplicationCallPipeline.Features) {
        println(":::::::::::::::::::configureSecurity execute 2")
        if(call.sessions.get<ChatSession>() == null) {
            val userPhone = call.parameters["sender"] ?: "Guest"
            call.sessions.set(ChatSession(sender = userPhone, sessionId = generateNonce()))
        }
    }
}


///////////////////////////
//fun Application.configureSecurity(tokenConfig: AccessTokenConfig, hoconApplicationConfig: HoconApplicationConfig) {
//    authentication {
//        jwt {
//            realm = hoconApplicationConfig.property("jwt.realm_value").getString()
//            verifier(
//                JWT
//                    .require(Algorithm.HMAC256(tokenConfig.secret))
//                    .withAudience(tokenConfig.audience)
//                    .withIssuer(tokenConfig.issuer)
//                    .build()
//            )
//
//            validate { credential ->
//                if (credential.payload.audience.contains(tokenConfig.audience)) {
//                    JWTPrincipal(credential.payload)
//                } else null
//            }
//        }
//    }
//}
//////////////////////////////////
//fun Application.configureSecurity(config: TokenConfig) {
//    authentication {
//        jwt {
//            // realm` указывает на имя или идентификатор защищенной области.
//            // Значение для `realm` извлекается из конфигурации приложения
//            realm = this@configureSecurity.environment.config.property("realm_value").getString()
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
//}