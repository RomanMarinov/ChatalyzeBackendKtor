package ru.marinovdev

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.marinovdev.features.database.configureDatabase
import ru.marinovdev.plugins.*
import ru.marinovdev.routing.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)

//
//    val client = HttpClient {
//        install(WebSockets)
//    }
//    runBlocking {
//        client.webSocket(method = HttpMethod.Get, host = "192.168.0.100", port = 8080, path = "/chat") {
//            val messageOutputRoutine = launch { outputMessages() }
//            val userInputRoutine = launch { inputMessages() }
//
//            userInputRoutine.join() // Wait for completion; either "exit" or error
//            messageOutputRoutine.cancelAndJoin()
//        }
//    }
//    client.close()




  //  println("Connection closed. Goodbye!")
}

fun Application.module() {
    configureKoin()//
    configureDatabase()//
    configureContentNegotiation()//
    configureSocketsParams()//

    configureSocketConnectionAndMessagingRouting()//

    configureSecurity()

    configureAuthentication(HoconApplicationConfig(ConfigFactory.load()))

    configureRegisterRouting()//
    configureSignInRouting()

    configureForgotPasswordUserEmailRouting()
    configureForgotPasswordUserCodeRouting()
    configureForgotPasswordUserPasswordRouting()

    configureLogoutRouting()
    configureDeleteProfileRouting()
    // configureAuthenticate()
    // configureSecret()
}
//suspend fun DefaultClientWebSocketSession.outputMessages() {
//    try {
//        for (message in incoming) {
//            message as? Frame.Text ?: continue
//            println(message.readText())
//        }
//    } catch (e: Exception) {
//        println("Error while receiving: " + e.localizedMessage)
//    }
//}
//
//suspend fun DefaultClientWebSocketSession.inputMessages() {
//    while (true) {
//        val message = readLine() ?: ""
//        if (message.equals("exit", true)) return
//        try {
//            send(message)
//        } catch (e: Exception) {
//            println("Error while sending: " + e.localizedMessage)
//            return
//        }
//    }
//}

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