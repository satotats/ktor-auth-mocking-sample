package com.satotats.ktor

import io.ktor.application.*
import io.ktor.auth.*

fun Application.testAuth(loginUser: LoginUser? = null) {
    install(Authentication) {
        basic {
            authentication { }
            validate { return@validate loginUser?.id }
        }
    }
}

private const val mockAuthKey =  "Basic amV0YnJhaW5zOmZvb2Jhcg"
fun anyAuthKey() = mockAuthKey