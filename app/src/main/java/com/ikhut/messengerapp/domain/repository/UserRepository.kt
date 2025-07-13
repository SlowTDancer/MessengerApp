package com.ikhut.messengerapp.domain.repository

import com.ikhut.messengerapp.domain.model.User

interface UserRepository {
    suspend fun registerUser(user: User): Result<Unit>
    suspend fun loginUser(username: String, password: String): Result<User>
    suspend fun updateUser(username: String, user: User): Result<Unit>
}