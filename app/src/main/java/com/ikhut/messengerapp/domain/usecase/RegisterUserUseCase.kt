package com.ikhut.messengerapp.domain.usecase

import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.UserRepository

class RegisterUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return repository.registerUser(user)
    }
}