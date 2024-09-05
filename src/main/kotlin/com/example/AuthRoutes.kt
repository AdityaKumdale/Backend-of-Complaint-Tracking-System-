package com.example

import com.example.data.requests.AuthRequest
import com.example.data.responses.AuthResponse
import com.example.data.user.User
import com.example.data.user.UserDataSource
import com.example.security.hashing.HashingServices
import com.example.security.hashing.SaltedHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.commons.codec.digest.DigestUtils

//signup -> enter:name,pass -> pass transform to hash and salt -> store name,hash,salt in db
//signin ->

fun Route.signUp(
    hashingService: HashingServices,
    userDataSource: UserDataSource
) {
    post("signup") {
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val isPwTooShort = request.password.length < 8
        if(areFieldsBlank || isPwTooShort) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        //If the username and password pass the validation, it uses the hashingService to generate a salted hash from the password.
        //It creates a User object using the username, hashed password, and salt, and then attempts to insert this user into the data source (userDataSource).
        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            username = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        val wasAcknowledged = userDataSource.insertUser(user)
        if(!wasAcknowledged)  {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    userDataSource: UserDataSource,
    hashingService: HashingServices,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        //user retrival
        val user = userDataSource.getUserByUsername(request.username)
        if(user == null) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        //compares the hash of the entered password with the stored hash and salt in the user's record.
        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt      //will prepend to request.password ,hash it and chechk if its eq to stored hash in db
            )
        )
        if(!isValidPassword) {
            println("Entered hash: ${DigestUtils.sha256Hex("${user.salt}${request.password}")}, Hashed PW: ${user.password}")
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        // it uses the tokenService to generate a token for the user, including the user's ID as a claim.
        val token = tokenService.generate( //save value in token
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(     //attach the token to respose and save it in prefs
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

//stay logged in
fun Route.authenticate() { //check if token saved in prefs is still valid one
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}