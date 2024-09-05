package com.example.security.token

//represents individual claims that are added to the token payload.
//key value pair used to store user info in token
data class TokenClaim(
    val name: String,   //user id
    val value: String  //acutal id
)