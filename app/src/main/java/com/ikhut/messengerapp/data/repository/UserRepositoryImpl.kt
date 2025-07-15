package com.ikhut.messengerapp.data.repository

import com.ikhut.messengerapp.data.firebase.FirebaseUserDataSource
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.PaginatedResult
import com.ikhut.messengerapp.domain.repository.UserRepository

class UserRepositoryImpl(
    private val dataSource: FirebaseUserDataSource
) : UserRepository {

    override suspend fun addUser(user: User): Result<Unit> {
        return dataSource.createUser(user)
    }

    override suspend fun updateUser(username: String, user: User): Result<Unit> {
        return dataSource.updateUser(username, user)
    }

    override suspend fun getUser(username: String): Result<User> {
        return dataSource.getUser(username)
    }

    override suspend fun getUsersWithCursor(pageSize: Int, lastUsername: String?): Result<PaginatedResult<User>> {
        return dataSource.getUsersWithCursor(pageSize, lastUsername)
    }
}
