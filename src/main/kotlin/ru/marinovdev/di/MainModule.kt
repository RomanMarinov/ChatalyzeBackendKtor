package ru.marinovdev.di

import io.ktor.server.application.*
import org.koin.dsl.module
import ru.marinovdev.data.code.CodeEntity
import ru.marinovdev.data.firebase.FirebaseRegisterEntity
import ru.marinovdev.data.messages.MessageEntity
import ru.marinovdev.data.tokens.TokensEntity
import ru.marinovdev.data.users.UsersEntity
import ru.marinovdev.data.users_session.UserSocketConnectionEntity
import ru.marinovdev.features.auth_lackner.security.hashing_code.HashingCodeService
import ru.marinovdev.features.auth_lackner.security.hashing_code.HashingCodeServiceImpl
import ru.marinovdev.features.auth_lackner.security.hashing_password.HashingService
import ru.marinovdev.features.auth_lackner.security.hashing_password.HashingServiceImpl
import ru.marinovdev.features.auth_lackner.security.token.JwtTokenService
import ru.marinovdev.features.auth_lackner.security.token.JwtTokenServiceImpl
import ru.marinovdev.features.jwt_token.TokenConfig

val mainModule = module {
    single<ApplicationCall> { getKoin().get<ApplicationCall>() }

    single<HashingService> { HashingServiceImpl() }
    single<HashingCodeService> { HashingCodeServiceImpl() }
    single<JwtTokenService> { JwtTokenServiceImpl() }

    single { UsersEntity }
    single { TokensEntity }
    single { CodeEntity }

    single { TokenConfig() }

    single { MessageEntity }
    single { UserSocketConnectionEntity }

    single { FirebaseRegisterEntity }

}