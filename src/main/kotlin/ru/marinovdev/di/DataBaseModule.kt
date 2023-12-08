package ru.marinovdev.di

import org.koin.dsl.module
import ru.marinovdev.features.database.DatabaseConnection

val dataBaseModule = module {
    single { DatabaseConnection() }
}