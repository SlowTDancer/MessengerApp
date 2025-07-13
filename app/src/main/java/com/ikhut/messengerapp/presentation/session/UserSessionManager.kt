package com.ikhut.messengerapp.presentation.session

import android.content.Context
import android.content.SharedPreferences
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserSessionManager(
    context: Context, private val userRepository: UserRepository
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var currentUser: User? = null

    var isLoggedIn: Boolean = false

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
            putString("username", username)
            apply()
        }
    }

    private fun loadUserSession() {
        val savedUsername = prefs.getString("username", null)

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
//                TODO
            }
        }
    }

    private fun clearUserSession() {
        prefs.edit().clear().apply()
    }

    fun getCurrentUsername(): String? = currentUser?.username
    fun getCurrentJob(): String? = currentUser?.job

    suspend fun refreshUserData(): Result<User> {
        val username = prefs.getString("username", null)
        return if (username != null) {
            val result = userRepository.getUser(username)
            result.onSuccess { user ->
                currentUser = user
            }
            result
        } else {
            Result.failure(Exception("No user logged in"))
        }
    }
}