package com.satotats.ktor

import io.ktor.application.*

fun Application.testModule(loginUser: LoginUser? = null) {
    testAuth(loginUser)
    others()
}
