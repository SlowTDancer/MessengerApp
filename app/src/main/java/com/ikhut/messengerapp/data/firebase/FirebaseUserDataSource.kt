package com.ikhut.messengerapp.data.firebase

import com.google.firebase.database.FirebaseDatabase
import com.ikhut.messengerapp.domain.model.User
import kotlinx.coroutines.tasks.await

class FirebaseUserDataSource {
    private val usersDB = FirebaseDatabase.getInstance().getReference("users")

    suspend fun createUser(user: User): Result<Unit> {
        val snapshot = usersDB.child(user.username).get().await()

        return if (snapshot.exists()) {
            Result.failure(Exception("Username already exists"))
        } else {
            usersDB.child(user.username).setValue(user).await()
            Result.success(Unit)
        }
    }

    suspend fun getUser(username: String): Result<User> {
        val snapshot = usersDB.child(username).get().await()
        val user = snapshot.getValue(User::class.java)

        return if (user != null) Result.success(user)
        else Result.failure(Exception("User not found"))
    }

    suspend fun updateUser(username: String, user: User): Result<Unit> {
        return try {
            val snapshot = usersDB.child(username).get().await()

            if (snapshot.exists()) {
                usersDB.child(username).setValue(user).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}