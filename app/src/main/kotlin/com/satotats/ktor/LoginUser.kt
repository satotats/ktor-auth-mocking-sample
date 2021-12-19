package com.satotats.ktor

import io.ktor.auth.*

data class LoginUser(val id: UserId, val name: String)
data class UserId(val value: String) : Principal