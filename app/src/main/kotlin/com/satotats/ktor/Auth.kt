package com.satotats.ktor

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import java.util.concurrent.TimeUnit

fun Application.auth() {
    install(Authentication) {
        // this is just a sample copied from https://ktor.io/docs/jwt.html#configure-verifier
        val issuer = environment.config.property("jwt.issuer").getString()
        val audience = environment.config.property("jwt.audience").getString()
        val myRealm = environment.config.property("jwt.realm").getString()

        val jwkProvider = JwkProviderBuilder(issuer)
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()

        jwt {
            realm = myRealm
            verifier(jwkProvider, issuer)
            validate { credential ->
                credential.payload.getClaim("user_id").asString()
                    .takeIf { it.isNotBlank() }
                    ?.let { UserId(it) }
            }
        }
    }
}
