package com.ikhut.messengerapp.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_USER_PREFS)

class UserSessionManager(
    private val context: Context, private val userRepository: UserRepository
) {
    companion object {
        private val USERNAME_KEY = stringPreferencesKey(Constants.PARAM_USERNAME)
    }

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
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit { prefs ->
                prefs[USERNAME_KEY] = username
            }
        }
    }

    private fun loadUserSession() {
        CoroutineScope(Dispatchers.IO).launch {
            val savedUsername = context.dataStore.data.first()[USERNAME_KEY]
            if (savedUsername != null) {
                isLoggedIn = true
                currentUser = User(username = savedUsername)
                loadUserFromRepository(savedUsername)
            } else {
                isLoggedIn = false
                currentUser = null
            }
        }
    }

    private fun loadUserFromRepository(username: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = userRepository.getUser(username)
                result.fold(onSuccess = { user -> currentUser = user },
                    onFailure = { logoutUser() })
            } catch (e: Exception) {
//                TODO
            }
        }
    }

    private fun clearUserSession() {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit { it.clear() }
        }
    }

    fun getCurrentUsername(): String? = currentUser?.username
    fun getCurrentJob(): String? = currentUser?.job

    suspend fun refreshUserData(): Result<User> {
        val username = context.dataStore.data.first()[USERNAME_KEY]
        return if (username != null) {
            val result = userRepository.getUser(username)
            result.onSuccess { user -> currentUser = user }
            result
        } else {
            Result.failure(Exception(Constants.ERROR_NO_USER_LOGGED_IN))
        }
    }
}
