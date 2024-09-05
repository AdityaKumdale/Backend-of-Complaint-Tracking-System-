package com.example.data.user

interface UserDataSource {
    suspend fun getUserByUsername(username: String): User?
    suspend fun insertUser(user: User): Boolean
}

//abstraction
//codebase will rely on this and not on implementation
//these are conditions must be met to access the source, don't know how they work though