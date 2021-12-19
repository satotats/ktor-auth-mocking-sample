package com.satotats.ktor

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*

@Suppress("unused")
fun Application.mainModule() {
    auth()
    others()
}

/** app features other than auth */
fun Application.others() {
    router()
    // ...and other modules
}

private fun Application.router() {
    routing {
        authenticate {
            get("/hello") {
                val loginUserId = call.principal<UserId>()!!
                call.respond("Hello, ${loginUserId.value}")
            }
        }
    }
}