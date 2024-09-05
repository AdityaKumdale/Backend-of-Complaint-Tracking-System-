package com.example.security.token

//is used to provide configuration settings for the token generation process
data class TokenConfig(
    val issuer: String,       //who issued token - server
    val audience: String,     //ntended recipient of the token),
    val expiresIn: Long,
    val secret: String
)

//fields needed for our jwt token config