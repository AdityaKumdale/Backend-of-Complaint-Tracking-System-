package com.example.plugins

import com.example.authenticate
import com.example.data.user.UserDataSource
import com.example.getSecretInfo
import com.example.security.hashing.HashingServices
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import com.example.signIn
import com.example.signUp
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingServices,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
//        get("/") {
//            call.respondText("Hello World!")
//        }
        signIn(userDataSource, hashingService, tokenService, tokenConfig)
        signUp(hashingService, userDataSource)
        authenticate()
        getSecretInfo()
    }
}
