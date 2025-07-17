package com.ikhut.messengerapp.data.session

import android.content.Context
import android.content.SharedPreferences
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserSessionManager(
    context: Context, private val userRepository: UserRepository
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_USER, Context.MODE_PRIVATE)

    var currentUser: User? = null
        private set

    var isLoggedIn: Boolean = false
        private set

    init {
        loadUserSession()
    }

    fun loginUser(user: User) {
        currentUser = user
        isLoggedIn = true
        saveUsername(user.username)
    }

    fun logoutUser() {
        currentUser = null
        isLoggedIn = false
        clearUserSession()
    }

    fun updateUser(user: User) {
        logoutUser()
        loginUser(user)
    }

    private fun saveUsername(username: String) {
        prefs.edit().apply {
            putString(Constants.PARAM_USERNAME, username)
            apply()
        }
    }

    private fun loadUserSession() {
        val savedUsername = prefs.getString(Constants.PARAM_USERNAME, null)

        if (savedUsername != null) {
            isLoggedIn = true
            currentUser = User(username = savedUsername)
            loadUserFromRepository(savedUsername)
        } else {
            isLoggedIn = false
            currentUser = null
        }
    }

    private fun loadUserFromRepository(username: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = userRepository.getUser(username)
                result.fold(onSuccess = { user ->
                    currentUser = user
                }, onFailure = {
                    logoutUser()
                })
            } catch (e: Exception) {
                logoutUser()
            }
        }
    }

    private fun clearUserSession() {
        prefs.edit().clear().apply()
    }

    fun getCurrentUsername(): String? = currentUser?.username

    suspend fun refreshUserData(): Result<User> {
        val username = prefs.getString(Constants.PARAM_USERNAME, null)
        return if (username != null) {
            val result = userRepository.getUser(username)
            result.onSuccess { user ->
                currentUser = user
            }
            result
        } else {
            Result.failure(Exception(Constants.ERROR_NO_USER_LOGGED_IN))
        }
    }
}
