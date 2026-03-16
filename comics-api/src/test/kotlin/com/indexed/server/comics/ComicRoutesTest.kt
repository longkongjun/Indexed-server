package com.indexed.server.comics

import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComicRoutesTest {
    @Test
    fun `comics list returns paged items`() = testApplication {
        application { module() }
        val response = client.get("/android/comics") {
            parameter("page", 0)
            parameter("size", 2)
            parameter("sort", "rating")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"items\""))
    }

    @Test
    fun `invalid client returns bad request`() = testApplication {
        application { module() }
        val response = client.get("/unknown/comics")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `chapter content returns pages`() = testApplication {
        application { module() }
        val response = client.get("/ios/chapters/chapter-1001/content")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"pages\""))
    }
}
