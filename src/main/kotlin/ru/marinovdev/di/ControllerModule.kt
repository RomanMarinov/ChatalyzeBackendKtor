package ru.marinovdev.di

import org.koin.dsl.module
import ru.marinovdev.controller.*

val controllerModule = module {
    single { RegisterController(get(), get()) }

    single { SignInController(get(), get(), get(), get(), get()) }

    single { UpdateTokensController(get(), get(), get()) }

    single { LogoutController(get()) }

    single { DeleteProfileController(get(), get(), get()) }

    single { UserCodeController(get(), get(), get()) }
    single { UserEmailController(get(), get(), get()) }
    single { UserPasswordController(get(), get()) }

    single { SocketMessageController(get(), get(), get()) }
    single { SocketStateUserController(get(), get()) }

    single { FirebaseRegisterController(get()) }
    single { FirebaseCommandController(get()) }
}