package com.ikhut.messengerapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.data.session.UserSessionManager
import com.ikhut.messengerapp.domain.common.Resource
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.UserRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userRepository: UserRepository, private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _updateProfileState = MutableLiveData<Resource<User>>()
    val updateProfileState: LiveData<Resource<User>> = _updateProfileState

    private val _updateProfilePictureState = MutableLiveData<Resource<User>>()
    val updateProfilePictureState: LiveData<Resource<User>> = _updateProfilePictureState

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
            _updateProfileState.value = Resource.Error(Constants.ERROR_NICKNAME_CANNOT_BE_EMPTY)
            return
        }

        if (job.trim().isEmpty()) {
            _updateProfileState.value = Resource.Error(Constants.ERROR_JOB_CANNOT_BE_EMPTY)
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
                    _updateProfileState.value =
                        Resource.Error(exception.message ?: Constants.ERROR_UPDATE_FAILED)
                })
            } catch (e: Exception) {
                _updateProfileState.value = Resource.Error(e.message ?: Constants.ERROR_NETWORK)
            }
        }
    }

    fun updateUserLocalImagePath(localImagePath: String) {
        val currentUser = userSessionManager.currentUser ?: return

        _updateProfilePictureState.value = Resource.Loading()

        val updatedUser = currentUser.copy(localImagePath = localImagePath)

        viewModelScope.launch {
            try {
                val result = userRepository.updateUser(currentUser.username, updatedUser)
                result.fold(onSuccess = {
                    userSessionManager.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                    _updateProfilePictureState.value = Resource.Success(updatedUser)
                }, onFailure = {
                    userSessionManager.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                    _updateProfilePictureState.value = Resource.Success(updatedUser)
                })
            } catch (e: Exception) {
                userSessionManager.updateUser(updatedUser)
                _currentUser.value = updatedUser
                _updateProfilePictureState.value = Resource.Success(updatedUser)
            }
        }
    }


    fun updateUserImageUrl(imageUrl: String) {
        val currentUser = userSessionManager.currentUser ?: return

        _updateProfilePictureState.value = Resource.Loading()

        val updatedUser = currentUser.copy(
            imageUrl = imageUrl, localImagePath = null
        )

        viewModelScope.launch {
            try {
                val result = userRepository.updateUser(currentUser.username, updatedUser)
                result.fold(onSuccess = {
                    userSessionManager.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                    _updateProfilePictureState.value = Resource.Success(updatedUser)
                }, onFailure = { exception ->
                    _updateProfilePictureState.value =
                        Resource.Error(exception.message ?: "Failed to update profile picture")
                })
            } catch (e: Exception) {
                _updateProfilePictureState.value =
                    Resource.Error(e.message ?: Constants.ERROR_NETWORK)
            }
        }
    }

    fun updateUserImageResource(imageRes: Int) {
        val currentUser = userSessionManager.currentUser ?: return

        _updateProfilePictureState.value = Resource.Loading()

        val updatedUser = currentUser.copy(
            imageRes = imageRes, imageUrl = null, localImagePath = null
        )

        viewModelScope.launch {
            try {
                val result = userRepository.updateUser(currentUser.username, updatedUser)
                result.fold(onSuccess = {
                    userSessionManager.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                    _updateProfilePictureState.value = Resource.Success(updatedUser)
                }, onFailure = {
                    userSessionManager.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                    _updateProfilePictureState.value = Resource.Success(updatedUser)
                })
            } catch (e: Exception) {
                userSessionManager.updateUser(updatedUser)
                _currentUser.value = updatedUser
                _updateProfilePictureState.value = Resource.Success(updatedUser)
            }
        }
    }

    private fun refreshUserData() {
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
                    return SettingsViewModel(userRepository, userSessionManager) as T
                }
            }
        }
    }
}