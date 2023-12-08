package ru.marinovdev.plugins

import io.ktor.server.application.*
import org.koin.core.context.startKoin
import ru.marinovdev.di.controllerModule
import ru.marinovdev.di.dataBaseModule
import ru.marinovdev.di.mainModule
import ru.marinovdev.di.repositoryModule

fun Application.configureKoin() {
    startKoin {
        modules(dataBaseModule)
        modules(controllerModule)
        modules(repositoryModule)
        modules(mainModule)
    }
}