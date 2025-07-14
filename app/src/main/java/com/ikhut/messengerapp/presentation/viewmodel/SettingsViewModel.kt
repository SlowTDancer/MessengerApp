package com.ikhut.messengerapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.UserRepository
import com.ikhut.messengerapp.data.session.UserSessionManager
import com.ikhut.messengerapp.domain.common.Resource
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userRepository: UserRepository, private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _updateProfileState = MutableLiveData<Resource<User>>()
    val updateProfileState: LiveData<Resource<User>> = _updateProfileState

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        _currentUser.value = userSessionManager.currentUser

        if (userSessionManager.currentUser?.username?.isEmpty() == true) {
            refreshUserData()
        }
    }

    fun updateProfile(username: String, job: String) {
        val currentUser = userSessionManager.currentUser

        if (username.trim().isEmpty()) {
            _updateProfileState.value = Resource.Error("Nickname cannot be empty")
            return
        }

        if (job.trim().isEmpty()) {
            _updateProfileState.value = Resource.Error("Job cannot be empty")
            return
        }

        _updateProfileState.value = Resource.Loading()

        val updatedUser = currentUser!!.copy(
            username = username.trim(), job = job.trim()
        )

        viewModelScope.launch {
            try {
                val result = userRepository.updateUser(currentUser.username, updatedUser)
                result.fold(onSuccess = {
                    userSessionManager.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                    _updateProfileState.value = Resource.Success(updatedUser)
                }, onFailure = { exception ->
                    _updateProfileState.value = Resource.Error(exception.message ?: "Update failed")
                })
            } catch (e: Exception) {
                _updateProfileState.value = Resource.Error(e.message ?: "Network error")
            }
        }
    }

    fun refreshUserData() {
        viewModelScope.launch {
            try {
                val result = userSessionManager.refreshUserData()
                result.onSuccess { user ->
                    _currentUser.value = user
                }
            } catch (e: Exception) {
//              TODO
            }
        }
    }

    fun signOut() {
        userSessionManager.logoutUser()
    }

    companion object {
        fun create(
            userRepository: UserRepository, userSessionManager: UserSessionManager
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                        return SettingsViewModel(userRepository, userSessionManager) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}