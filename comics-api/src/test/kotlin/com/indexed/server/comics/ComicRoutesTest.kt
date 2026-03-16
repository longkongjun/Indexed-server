package com.indexed.server.comics

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ComicRoutesTest {
    @Test
    fun `invalid sort returns bad request`() = testApplication {
        application { module() }
        val response = client.get("/android/comics") {
            url {
                parameters.append("sort", "unknown")
            }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `invalid client returns bad request`() = testApplication {
        application { module() }
        val response = client.get("/unknown/comics")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `invalid chapter id returns not found`() = testApplication {
        application { module() }
        val response = client.get("/ios/chapters/chapter-1001/content")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
