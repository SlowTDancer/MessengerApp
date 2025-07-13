package com.ikhut.messengerapp.domain.repository

import com.ikhut.messengerapp.domain.model.User

interface UserRepository {
    suspend fun addUser(user: User): Result<Unit>
    suspend fun updateUser(username: String, user: User): Result<Unit>
    suspend fun getUser(username: String): Result<User>
}