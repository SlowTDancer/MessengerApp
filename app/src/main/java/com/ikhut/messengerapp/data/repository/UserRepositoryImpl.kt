package com.ikhut.messengerapp.data.repository

import com.ikhut.messengerapp.data.firebase.FirebaseUserDataSource
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.UserRepository

class UserRepositoryImpl(
    private val dataSource: FirebaseUserDataSource
) : UserRepository {

    override suspend fun registerUser(user: User): Result<Unit> {
        return dataSource.createUser(user)
    }

    override suspend fun loginUser(username: String, password: String): Result<User> {
        return dataSource.getUser(username).fold(onSuccess = { user ->
            if (user.password == password) {
                Result.success(user)
            } else {
                Result.failure(Exception("Wrong password"))
            }
        }, onFailure = { error ->
            Result.failure(error)
        })
    }
}
