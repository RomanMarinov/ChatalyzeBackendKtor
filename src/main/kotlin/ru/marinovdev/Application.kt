package ru.marinovdev

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.marinovdev.features.database.configureDatabase
import ru.marinovdev.features.firebase.FirebaseAdmin
import ru.marinovdev.plugins.*
import ru.marinovdev.routing.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    configureKoin()
    configureDatabase()
    configureContentNegotiation()
    configureSocketsParams()

    configureAuthentication()

    configureSocketConnectionAndMessagingRouting()

    configureSecurity()

    configureUpdateTokensRouting()

    configureRegisterRouting(HoconApplicationConfig(ConfigFactory.load()))
    configureSignInRouting(HoconApplicationConfig(ConfigFactory.load()))

    FirebaseAdmin.init()
    configureFirebaseRegisterRouting()
    configureFirebaseCommandRouting()

    configureForgotPasswordUserEmailRouting()
    configureForgotPasswordUserCodeRouting()
    configureForgotPasswordUserPasswordRouting(HoconApplicationConfig(ConfigFactory.load()))

    configureLogoutRouting()
    configureDeleteProfileRouting()
}