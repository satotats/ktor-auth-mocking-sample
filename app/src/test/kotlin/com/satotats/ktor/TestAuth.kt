package com.satotats.ktor

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*

fun Application.testAuth(loginUser: LoginUser? = null) {
    install(Authentication) {
        mock {
            principal { loginUser?.id }
        }
    }
}


fun Authentication.Configuration.mock(
    name: String? = null,
    configure: MockAuthenticationProvider.Configuration.() -> Unit
) {
    val provider = MockAuthenticationProvider.Configuration(name).apply(configure).build()

    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val principal = provider.principalProvider(call)

        if (principal == null) {
            // completing context.challenge is necessary,
            // without that authentication will not fail
            // even when the principal is null
            call.respond(UnauthorizedResponse())
            context.challenge.complete()
            return@intercept
        }

        context.principal(principal)
    }
    register(provider)
}

class MockAuthenticationProvider(config: Configuration) : AuthenticationProvider(config) {
    val principalProvider = config.principalProvider

    class Configuration(name: String?) : AuthenticationProvider.Configuration(name) {
        var principalProvider: ApplicationCall.() -> Principal? = { null }

        fun build() = MockAuthenticationProvider(this)

        /**
         * if principalProvider returns null,
         * authentication will fail(401 response will be returned)
         * */
        fun principal(principalProvider: ApplicationCall.() -> Principal?) {
            this.principalProvider = principalProvider
        }
    }
}
