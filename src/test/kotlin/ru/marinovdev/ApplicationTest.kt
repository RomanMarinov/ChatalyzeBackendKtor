package ru.marinovdev

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import ru.marinovdev.routing.configureSocketConnectionAndMessagingRouting
import kotlin.test.*
import kotlin.test.Test

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureSocketConnectionAndMessagingRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("hi", bodyAsText())
        }
    }
}
