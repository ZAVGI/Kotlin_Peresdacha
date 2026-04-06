package com.peresdacha.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity(secret: String) {
    authentication {
        jwt("auth-jwt") {
            verifier(JWT.require(Algorithm.HMAC256(secret)).build())
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asLong()
                if (userId != null) io.ktor.server.auth.jwt.JWTPrincipal(credential.payload) else null
            }
        }
    }
}
