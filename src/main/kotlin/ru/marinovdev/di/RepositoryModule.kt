package ru.marinovdev.di

import org.koin.dsl.module
import ru.marinovdev.data.code.CodeDataSourceRepositoryImpl
import ru.marinovdev.data.messages.MessageDataSourceRepositoryImpl
import ru.marinovdev.data.tokens.TokensDataSourceRepositoryImpl
import ru.marinovdev.data.users.UsersDataSourceRepositoryImpl
import ru.marinovdev.data.users_session.UserSessionDataSourceRepositoryImpl
import ru.marinovdev.domain.repository.*

val repositoryModule = module {
    single<UsersDataSourceRepository> { UsersDataSourceRepositoryImpl(get()) }
    single<TokensDataSourceRepository> { TokensDataSourceRepositoryImpl(get()) }
    single<CodeDataSourceRepository> { CodeDataSourceRepositoryImpl(get()) }

    single<MessageDataSourceRepository> { MessageDataSourceRepositoryImpl(get()) }
    single<UserSessionDataSourceRepository> { UserSessionDataSourceRepositoryImpl(get()) }
}