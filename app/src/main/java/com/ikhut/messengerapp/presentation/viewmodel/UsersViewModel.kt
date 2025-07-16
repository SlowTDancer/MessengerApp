package com.ikhut.messengerapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository, private val loggedInUsername: String
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _currentQuery = MutableStateFlow("")
    val currentQuery: StateFlow<String> = _currentQuery.asStateFlow()

    private var lastUsername: String? = null

    init {
        loadMore()
    }

    fun setSearchQuery(query: String) {
        val trimmedQuery = query.trim()

        if (_currentQuery.value == trimmedQuery) return

        val previousQuery = _currentQuery.value
        _currentQuery.value = trimmedQuery

        when {
            trimmedQuery.length < Constants.MIN_SEARCH_LENGTH && previousQuery.length >= Constants.MIN_SEARCH_LENGTH -> {

                resetAndLoad()
            }

            trimmedQuery.length >= Constants.MIN_SEARCH_LENGTH -> {
                resetAndLoad()
            }
        }
    }

    fun loadMore() {
        if (_isLoading.value || !_hasMore.value) return

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val query = _currentQuery.value
                val result = if (query.length >= Constants.MIN_SEARCH_LENGTH) {
                    repository.searchUsersWithCursor(query, Constants.PAGE_SIZE, lastUsername)
                } else {
                    repository.getUsersWithCursor(Constants.PAGE_SIZE, lastUsername)
                }

                result.fold(onSuccess = { paginatedResult ->
                    val filteredUsers = paginatedResult.data.filter { user ->
                        user.username != loggedInUsername
                    }

                    _users.value = _users.value + filteredUsers
                    _hasMore.value = paginatedResult.hasNext
                    lastUsername = paginatedResult.nextPageToken
                }, onFailure = { error ->
                    _errorState.value = error.message ?: Constants.ERROR_UNKNOWN
                })
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun resetAndLoad() {
        lastUsername = null
        _users.value = emptyList()
        _hasMore.value = true
        loadMore()
    }

    companion object {
        fun create(
            userRepository: UserRepository, loggedInUsername: String
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