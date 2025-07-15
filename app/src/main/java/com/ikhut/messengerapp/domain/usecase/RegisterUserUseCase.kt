package com.ikhut.messengerapp.domain.usecase

import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.UserRepository

class RegisterUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User): Result<Unit> {
        val username = user.username.trim()
        val job = user.job.trim()
        val password = user.password.trim()

        if (username.isEmpty()) {
            return Result.failure(Exception(Constants.ERROR_USERNAME_CANNOT_BE_EMPTY))
        }

        if (job.isEmpty()) {
            return Result.failure(Exception(Constants.ERROR_JOB_CANNOT_BE_EMPTY))
        }

        if (password.length < Constants.MIN_PASSWORD_SIZE) {
            return Result.failure(Exception(Constants.ERROR_PASSWORD_CHECK))
        }

        val existingUserResult = repository.getUser(user.username.trim())
        if (existingUserResult.isSuccess) {
            return Result.failure(Exception(Constants.ERROR_USER_ALREADY_EXISTS))
        }

        val cleanUser = user.copy(
            username = username, job = job, password = password
        )

        return repository.addUser(cleanUser)
    }
}