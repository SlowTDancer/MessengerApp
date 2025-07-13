package com.ikhut.messengerapp.domain.usecase

import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.UserRepository

class LoginUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(username: String, password: String): Result<User> {
        return repository.getUser(username).fold(onSuccess = { user ->
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
