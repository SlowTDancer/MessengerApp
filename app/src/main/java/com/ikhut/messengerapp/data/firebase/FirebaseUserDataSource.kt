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

    suspend fun updateUser(oldUsername: String, user: User): Result<Unit> {
        return try {
            val oldSnapshot = usersDB.child(oldUsername).get().await()

            if (!oldSnapshot.exists()) {
                return Result.failure(Exception("User not found"))
            }

            val newSnapshot = usersDB.child(user.username).get().await()
            if (newSnapshot.exists() && oldUsername != user.username) {
                return Result.failure(Exception("New username already exists"))
            }

            usersDB.child(user.username).setValue(user).await()

            if (oldUsername != user.username) {
                usersDB.child(oldUsername).removeValue().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}