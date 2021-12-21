package com.satotats.ktor

import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class AppTest {
    @Test
    fun `authenticated hello - success`() {
        withTestApplication({ testModule(loginUser = TestUser.Bezos) }) {
            handleRequest(HttpMethod.Get, "/hello") { }
                .apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertEquals("Hello, ${TestUser.Bezos.id.value}", response.content)

                    // "Hello, amzn001"
                    println(response.content)
                }
        }
    }

    @Test
    fun `authenticated hello - failure`() {
        withTestApplication({ testModule(/*loginUser = TestUser.Bezos*/) }) {
            handleRequest(HttpMethod.Get, "/hello") { }
                .apply {
                    assertEquals(HttpStatusCode.Unauthorized, response.status())
                    assertNull(response.content)
                }
        }
    }
}

object TestUser {
    val Bezos = LoginUser(UserId("amzn001"), "Jeff Bezos")
}
