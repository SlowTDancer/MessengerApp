package com.ikhut.messengerapp.domain.usecase

import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.UserRepository

class RegisterUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User): Result<Unit> {
        val username = user.username.trim()
        val job = user.job.trim()
        val password = user.password.trim()

        if (username.isEmpty()) {
            return Result.failure(Exception("Username cannot be empty"))
        }

        if (job.isEmpty()) {
            return Result.failure(Exception("Job cannot be empty"))
        }

        if (password.length < 4) {
            return Result.failure(Exception("Password must be at least 4 characters"))
        }

        val existingUserResult = repository.getUser(user.username.trim())
        if (existingUserResult.isSuccess) {
            return Result.failure(Exception("Username already exists"))
        }

        val cleanUser = user.copy(
            username = username, job = job, password = password
        )

        return repository.addUser(cleanUser)
    }
}