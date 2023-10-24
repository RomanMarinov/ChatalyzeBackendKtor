package ru.marinovdev

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.marinovdev.features.auth_lackner.security.hashing_code.SHA256HashingCodeService
import ru.marinovdev.features.auth_lackner.security.hashing_password.SHA256HashingService
import ru.marinovdev.features.auth_lackner.security.token.AccessTokenConfig
import ru.marinovdev.features.auth_lackner.security.token.JwtTokenService
import ru.marinovdev.features.auth_lackner.security.token.RefreshTokenConfig
import ru.marinovdev.features.database.configureDatabase
import ru.marinovdev.features.delete_profile.configureDeleteProfileRouting
import ru.marinovdev.features.forgot_password.user_code.configureForgotPasswordUserCodeRouting
import ru.marinovdev.features.forgot_password.user_email.configureForgotPasswordUserEmailRouting
import ru.marinovdev.features.forgot_password.user_password.configureForgotPasswordUserPasswordRouting
import ru.marinovdev.features.logout.configureLogoutRouting
import ru.marinovdev.features.register.configureRegisterRouting
import ru.marinovdev.features.sign_in.configureSignInRouting
import ru.marinovdev.plugins.configureContentNegotiation
import ru.marinovdev.plugins.configureSecurity

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {

    configureDatabase()

    val issuer = HoconApplicationConfig(ConfigFactory.load()).property("jwt.issuer").getString()
    val audience = HoconApplicationConfig(ConfigFactory.load()).property("jwt.audience").getString()
    val secret = HoconApplicationConfig(ConfigFactory.load()).property("jwt.secret").getString()

    val accessTokenConfig = AccessTokenConfig(
        issuer = issuer,
        audience = audience,
        expiresIn = 1L * 24L * 60L * 60L * 1000L,
        secret = secret
    )

    val refreshTokenConfig = RefreshTokenConfig(
        issuer = issuer,
        audience = audience,
        expiresIn = 30L * 24L * 60L * 60L * 1000L,
        secret = secret
    )

    val hashingService = SHA256HashingService()
    val jwtTokenService = JwtTokenService()
    val hashingCodeService = SHA256HashingCodeService()

    configureSignInRouting(
        hashingService = hashingService,
        jwtTokenService = jwtTokenService,
        accessTokenConfig = accessTokenConfig,
        refreshTokenConfig = refreshTokenConfig
    )
    configureRegisterRouting(hashingService = hashingService)
    configureLogoutRouting()
    // configureAuthenticate()
    // configureSecret()
    configureContentNegotiation()
    configureForgotPasswordUserEmailRouting(
        hashingCodeService = hashingCodeService
    )
    configureForgotPasswordUserCodeRouting(
        hashingCodeService = hashingCodeService
    )
    configureForgotPasswordUserPasswordRouting(hashingService = hashingService)
    configureSecurity(accessTokenConfig, HoconApplicationConfig(ConfigFactory.load()))
    configureDeleteProfileRouting(jwtTokenService = jwtTokenService)
}


///////////////////////////////////////////////////////

//fun Application.configureDependencies() {
//    // Сконфигурировать БД
//    val url = "jdbc:postgresql://localhost:5433/chatalyze_bd"
//    val user = "admin"
//    val password = "qwerty"
//
//    // Инициализация базы данных
//    Database.connect(
//        url = url,
//        driver = "org.postgresql.Driver",
//        user = user,
//        password = password
//    )
//
//}
//
//
//fun main(args: Array<String>) = io.ktor.server.cio.EngineMain.main(args)
//
//
//fun Application.module() {
//    configureDependencies()
//
////
////    try {
////        // Сконфигурировать БД
////        val url = "jdbc:postgresql://localhost:5433/chatalyze_bd"
////        //    val url = "jdbc:postgresql://n_car_fuel_calc_postgres:5432/car_fuel"
////        val user = "admin"
////        val password = "qwerty"
////
////        Database.connect(
////            url = url,
////            driver = "org.postgresql.Driver",
////            user = user,
////            password = password
////        )
////
////        io.ktor.server.engine.embeddedServer(
////            Netty,
////            port = 8080,
////            host = "0.0.0.0",
////            module = Application::module
////        )
////            .start(wait = true)
////    } catch (e: Exception) {
////        println("try catch fun main e=" + e)
////    }
//
//
//    // install(Authentication)
//// db
//    val tokenService = JwtTokenService()
////    val tokenConfig = TokenConfig(
////        issuer = environment.config.property("jwt.issuer").getString(),
////        audience = environment.config.property("http://0.0.0.0:8080").getString(),
////        expiresIn = 365L * 1000L * 60L * 60L * 24L,
////        secret = System.getenv("JWT_SECRET")
////    )
//
//    val tokenConfig = TokenConfig(
//        issuer = "http://0.0.0.0:8080",
//        audience = "http://0.0.0.0:8080",
//        expiresIn = 365L * 1000L * 60L * 60L * 24L,
//        secret = System.getenv("JWT_SECRET")
//    )
//
//    val hashingService = SHA256HashingService()
//
//    configureSignInRouting(hashingService = hashingService, tokenService = tokenService, tokenConfig = tokenConfig)
//    configureRegisterRouting(hashingService = hashingService)
//   // configureAuthenticate()
//   // configureSecret()
//    configureSerialization()
//    configureSenderEmailRouting()
//    configureSecurity(tokenConfig)
//}


///////////////////////////////////////////////////////


//fun main() {
//    try {
//        // Сконфигурировать БД
//        val url = "jdbc:postgresql://localhost:5433/chatalyze_bd"
//        //    val url = "jdbc:postgresql://n_car_fuel_calc_postgres:5432/car_fuel"
//        val user = "admin"
//        val password = "qwerty"
//
//        Database.connect(
//            url = url,
//            driver = "org.postgresql.Driver",
//            user = user,
//            password = password
//        )
//
//        embeddedServer(
//            Netty,
//            port = 8080,
//            host = "0.0.0.0",
//            module = Application::module
//        )
//            .start(wait = true)
//    } catch (e: Exception) {
//        println("try catch fun main e=" + e)
//    }
//}
//
//
//fun Application.module() {
//
//   // install(Authentication)
//// db
//    val tokenService = JwtTokenService()
////    val tokenConfig = TokenConfig(
////        issuer = environment.config.property("jwt.issuer").getString(),
////        audience = environment.config.property("http://0.0.0.0:8080").getString(),
////        expiresIn = 365L * 1000L * 60L * 60L * 24L,
////        secret = System.getenv("JWT_SECRET")
////    )
//
//
//    val tokenConfig = TokenConfig(
//        issuer = "http://0.0.0.0:8080",
//        audience = "http://0.0.0.0:8080",
//        expiresIn = 365L * 1000L * 60L * 60L * 24L,
//        secret = System.getenv("JWT_SECRET")
//    )
//
//    val hashingService = SHA256HashingService()
//
//    configureSignInRouting(hashingService = hashingService, tokenService = tokenService, tokenConfig = tokenConfig)
//    configureRegisterRouting(hashingService = hashingService)
//    //configureAuthenticate()
//    //configureSecret()
//    configureSerialization()
//    configureSenderEmailRouting()
//    configureSecurity(tokenConfig)
//}