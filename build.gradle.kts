val ktor_version: String by project
val kotlin_version: String by project
val koin_version: String by project
val logback_version: String by project
val exposed_version: String by project

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    id("io.ktor.plugin") version "2.3.3"
}

group = "ru.marinovdev"
version = "0.0.1"

application {
    mainClass.set("ru.marinovdev.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-cio-jvm")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    // преобразовывать объекты в формат JSON и обратно
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation ("com.google.code.gson:gson:2.8.8")

    // dependencies for postgres
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.postgresql:postgresql:42.6.0")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    // sending email
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("com.sun.mail:javax.mail:1.6.2")

    // auth jwt
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
//    implementation("io.ktor:ktor-auth:$ktor_version")
//    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
//    implementation("io.ktor:jwt:$ktor_version")
//
//    implementation("com.auth0:java-jwt:3.18.1")

    implementation("commons-codec:commons-codec:1.16.0")

    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")

    // websocket
    implementation("io.ktor:ktor-server-websockets-jvm:$ktor_version")

    // sessions
    implementation("io.ktor:ktor-server-sessions:$ktor_version")

    // Koin
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    //   implementation("io.insert-koin:koin-core-jvm:$koin_version")
    // implementation("io.insert-koin:koin-ktor:3.2.0")

    implementation("io.insert-koin:koin-ktor:$koin_version")

    implementation("org.jetbrains.exposed:exposed-java-time:0.30.1")
}
