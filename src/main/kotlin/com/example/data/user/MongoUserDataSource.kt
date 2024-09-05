package com.example.data.user

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq


//Actual implementation of  UserDataSource
class MongoUserDataSource(
    db: CoroutineDatabase
): UserDataSource {

    private val users = db.getCollection<User>() //automatically creates collection when called for first time

    override suspend fun getUserByUsername(username: String): User? {
        return users.findOne(User::username eq username)
    }

    override suspend fun insertUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }
}