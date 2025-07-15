package com.ikhut.messengerapp.domain.repository

import com.ikhut.messengerapp.domain.model.User

interface UserRepository {
    suspend fun addUser(user: User): Result<Unit>
    suspend fun updateUser(username: String, user: User): Result<Unit>
    suspend fun getUser(username: String): Result<User>

    suspend fun getUsersWithCursor(
        pageSize: Int,
        lastUsername: String? = null
    ): Result<PaginatedResult<User>>

    suspend fun searchUsersWithCursor(
        searchQuery: String,
        pageSize: Int,
        lastUsername: String?
    ): Result<PaginatedResult<User>>
}

data class PaginatedResult<T>(
    val data: List<T>,
    val hasNext: Boolean,
    val nextPageToken: String? = null
)