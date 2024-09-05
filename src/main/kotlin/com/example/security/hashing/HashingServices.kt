package com.example.security.hashing

interface HashingServices {
    fun generateSaltedHash(value: String, saltLength: Int = 32): SaltedHash
    fun verify(value: String, saltedHash: SaltedHash): Boolean
}