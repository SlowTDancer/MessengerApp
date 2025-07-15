package com.ikhut.messengerapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository,
    private val loggedInUsername: String
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private var lastUsername: String? = null
    private val pageSize = 20

    init {
        loadUsers()
    }

    fun loadUsers() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val cursor = lastUsername

                repository.getUsersWithCursor(pageSize, cursor).fold(
                    onSuccess = { result ->
                        val fetchedUser = result.data.filter { d -> d.username != loggedInUsername }
                        _users.value = _users.value + fetchedUser

                        _hasMore.value = result.hasNext
                        lastUsername = result.nextPageToken
                    },
                    onFailure = { error ->
                        // Handle error
                        // TODO:
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreUsers() {
        if (_hasMore.value && !_isLoading.value) {
            loadUsers()
        }
    }

    companion object {
        fun create(
            userRepository: UserRepository,
            loggedInUsername: String
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UserViewModel(userRepository, loggedInUsername) as T
                }
            }
        }
    }
}