package com.example.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtTokenService:TokenService {
    override fun generate(config: TokenConfig, vararg claims: TokenClaim): String {
        var token = JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn))
        claims.forEach { claim -> //iterates through each TokenClaim provided and adds them as claims to the token. It sets the claim name and value based on the TokenClaim objects.
            token = token.withClaim(claim.name, claim.value)
        }
        return token.sign(Algorithm.HMAC256(config.secret)) // signs the token using the HMAC256 algorithm with the secret key specified in the TokenConfig object and returns the resulting JWT token as a string.
    }
}