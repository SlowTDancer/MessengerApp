package com.ikhut.messengerapp.data.firebase

import com.google.firebase.database.FirebaseDatabase
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.PaginatedResult
import kotlinx.coroutines.tasks.await

class FirebaseUserDataSource {
    private val usersDB = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS)

    suspend fun createUser(user: User): Result<Unit> {
        val snapshot = usersDB.child(user.username).get().await()

        return if (snapshot.exists()) {
            Result.failure(Exception(Constants.ERROR_USER_ALREADY_EXISTS))
        } else {
            usersDB.child(user.username).setValue(user).await()
            Result.success(Unit)
        }
    }

    suspend fun getUser(username: String): Result<User> {
        val snapshot = usersDB.child(username).get().await()
        val user = snapshot.getValue(User::class.java)

        return if (user != null) Result.success(user)
        else Result.failure(Exception(Constants.ERROR_USER_NOT_FOUND))
    }

    suspend fun updateUser(oldUsername: String, user: User): Result<Unit> {
        return try {
            val oldSnapshot = usersDB.child(oldUsername).get().await()

            if (!oldSnapshot.exists()) {
                return Result.failure(Exception(Constants.ERROR_USER_NOT_FOUND))
            }

            val newSnapshot = usersDB.child(user.username).get().await()
            if (newSnapshot.exists() && oldUsername != user.username) {
                return Result.failure(Exception(Constants.ERROR_USER_ALREADY_EXISTS))
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

    suspend fun getUsersWithCursor(
        pageSize: Int, lastUsername: String? = null
    ): Result<PaginatedResult<User>> {
        return try {
            val query = if (lastUsername == null) {
                usersDB.orderByChild(Constants.PARAM_USERNAME).limitToFirst(pageSize + 1)
            } else {
                usersDB.orderByChild(Constants.PARAM_USERNAME).startAfter(lastUsername)
                    .limitToFirst(pageSize + 1)
            }

            val snapshot = query.get().await()
            val users = mutableListOf<User>()

            snapshot.children.forEach { child ->
                child.getValue(User::class.java)?.let { user ->
                    users.add(user)
                }
            }

            val hasNext = users.size > pageSize
            if (hasNext) {
                users.removeAt(users.size - 1)
            }

            val result = PaginatedResult(
                data = users, hasNext = hasNext, nextPageToken = users.lastOrNull()?.username
            )

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchUsersWithCursor(
        searchQuery: String, pageSize: Int, lastUsername: String? = null
    ): Result<PaginatedResult<User>> {
        return try {
            val endQuery = searchQuery + "\uf8ff"

            val query = if (lastUsername == null) {
                usersDB.orderByChild(Constants.PARAM_USERNAME).startAt(searchQuery).endAt(endQuery)
                    .limitToFirst(pageSize + 1)
            } else {
                usersDB.orderByChild(Constants.PARAM_USERNAME).startAfter(lastUsername)
                    .endAt(endQuery).limitToFirst(pageSize + 1)
            }

            val snapshot = query.get().await()
            val users = mutableListOf<User>()

            snapshot.children.forEach { child ->
                child.getValue(User::class.java)?.let { user ->
                    if (user.username.startsWith(searchQuery, ignoreCase = true)) {
                        users.add(user)
                    }
                }
            }

            val hasNext = users.size > pageSize
            if (hasNext) {
                users.removeAt(users.size - 1)
            }

            val result = PaginatedResult(
                data = users, hasNext = hasNext, nextPageToken = users.lastOrNull()?.username
            )

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}