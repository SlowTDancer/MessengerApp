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

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _currentQuery = MutableStateFlow("")
    val currentQuery: StateFlow<String> = _currentQuery.asStateFlow()

    private var lastUsername: String? = null
    private val pageSize = 20

    init {
        loadMore()
    }

    fun setSearchQuery(query: String) {
        val trimmedQuery = query.trim()

        if (_currentQuery.value == trimmedQuery) return

        val previousQuery = _currentQuery.value
        _currentQuery.value = trimmedQuery

        // Handle different query transition cases
        when {
            // Case 1: New query is too short (< 3 chars) but previous was valid search
            trimmedQuery.length < 3 && previousQuery.length >= 3 -> {
                // Reset to show all users
                resetAndLoad()
            }
            // Case 2: New query is valid length (>= 3 chars)
            trimmedQuery.length >= 3 -> {
                resetAndLoad()
            }
            // Case 3: New query is too short and previous was also too short
            // Do nothing - stay in current state
        }
    }

    // Single method to load more (works for both search and regular)
    fun loadMore() {
        if (_isLoading.value || !_hasMore.value) return

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val query = _currentQuery.value
                val result = if (query.length >= 3) {
                    repository.searchUsersWithCursor(query, pageSize, lastUsername)
                } else {
                    repository.getUsersWithCursor(pageSize, lastUsername)
                }

                result.fold(
                    onSuccess = { paginatedResult ->
                        val filteredUsers = paginatedResult.data.filter { user ->
                            user.username != loggedInUsername
                        }

                        _users.value = _users.value + filteredUsers
                        _hasMore.value = paginatedResult.hasNext
                        lastUsername = paginatedResult.nextPageToken
                    },
                    onFailure = { error ->
                        _errorState.value = error.message ?: "Unknown error"
                    }
                )
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