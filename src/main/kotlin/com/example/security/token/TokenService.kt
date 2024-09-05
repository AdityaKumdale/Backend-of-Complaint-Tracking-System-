package com.example.security.token

interface TokenService {
    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim //you can pass one or more TokenClaim objects to include different pieces of information about a single user in the generated token.
    ): String
}