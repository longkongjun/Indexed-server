package com.indexed.server

import io.ktor.server.config.MapApplicationConfig
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        initEnv()
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello World!", response.bodyAsText())
    }

    @Test
    fun testTest1() = testApplication {
        initEnv()
        val response = client.get("/test1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "Hello From Ktor")
    }

    @Test
    fun tasksCanBeFoundByPriority() = testApplication {
        initEnv()
        val response = client.get("/tasks/priority/Medium")
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(body, "Mow the lawn")
        assertContains(body, "Paint the fence")
    }

    @Test
    fun invalidPriorityProduces400() = testApplication {
        initEnv()
        val response = client.get("/tasks/priority/Invalid")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun unusedPriorityProduces404() = testApplication {
        initEnv()
        val response = client.get("/tasks/priority/Vital")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    private fun TestApplicationBuilder.initEnv() {
        environment {
            config = MapApplicationConfig(
                "indexed.source.root" to "/tmp/test-source",
                "indexed.scraping.organized" to "/tmp/test-organized"
            )
        }

        application { module() }
    }
}