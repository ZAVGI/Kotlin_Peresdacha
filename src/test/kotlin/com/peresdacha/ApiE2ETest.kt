package com.peresdacha

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiE2ETest {
    @Test
    fun `e2e health endpoint`() = testApplication {
        application { routing { get("/products") { call.respondText("[]") } } }
        val response = client.get("/products")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `e2e auth endpoint`() = testApplication {
        application { routing { post("/auth/login") { call.respondText("{\"token\":\"t\"}") } } }
        val response = client.post("/auth/login") { contentType(ContentType.Application.Json) }
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
