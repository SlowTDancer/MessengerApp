package com.ikhut.messengerapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.domain.common.Resource
import com.ikhut.messengerapp.domain.model.ConversationSummary
import com.ikhut.messengerapp.domain.model.toLocalDateTime
import com.ikhut.messengerapp.domain.repository.ConversationSummaryRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ConversationSummaryViewModel(
    private val conversationSummaryRepository: ConversationSummaryRepository,
    private val currentUserId: String
) : ViewModel() {

    private val _conversationsState = MutableLiveData<Resource<List<ConversationSummary>>>()
    val conversationsState: LiveData<Resource<List<ConversationSummary>>> = _conversationsState

    private val _conversations = MutableLiveData<List<ConversationSummary>>(emptyList())
    val conversations: LiveData<List<ConversationSummary>> = _conversations

    private var isLoadingMore = false
    private var allConversationsLoaded = false
    private var lastLoadedConversationTime: LocalDateTime? = null

    init {
        loadInitialConversations()
        observeConversationUpdates()
    }

    private fun loadInitialConversations() {
        _conversationsState.value = Resource.Loading()
        loadMoreConversations()
    }

    fun loadMoreConversations(limit: Int = Constants.LAZY_UPDATE_SIZE) {
        if (isLoadingMore || allConversationsLoaded) return

        isLoadingMore = true
        viewModelScope.launch {
            val result = conversationSummaryRepository.getRecentConversationSummaries(
                userId = currentUserId,
                limit = limit,
                lastConversationTime = lastLoadedConversationTime
            )

            result.fold(onSuccess = { newConversations ->
                if (newConversations.isEmpty()) {
                    allConversationsLoaded = true
                    if (_conversations.value?.isEmpty() == true) {
                        _conversationsState.value = Resource.Success(emptyList())
                    }
                } else {
                    lastLoadedConversationTime =
                        newConversations.last().lastMessageTime.toLocalDateTime()
                    val currentConversations = _conversations.value ?: emptyList()
                    val updatedConversations =
                        (currentConversations + newConversations).distinctBy { it.addresseeName }
                            .sortedByDescending { it.lastMessageTime }

                    _conversations.value = updatedConversations
                    _conversationsState.value = Resource.Success(updatedConversations)
                }
            }, onFailure = { exception ->
                _conversationsState.value = Resource.Error(
                    exception.message ?: Constants.ERROR_FAILED_TO_LOAD_CONVERSATIONS
                )
            })
            isLoadingMore = false
        }
    }

    private fun observeConversationUpdates() {
        viewModelScope.launch {
            conversationSummaryRepository.observeConversationUpdates(currentUserId)
                .collect { updatedConversation ->
                    val currentConversations =
                        _conversations.value?.toMutableList() ?: mutableListOf()
                    val existingIndex = currentConversations.indexOfFirst {
                        it.addresseeName == updatedConversation.addresseeName
                    }

                    if (existingIndex != -1) {
                        currentConversations[existingIndex] = updatedConversation
                    } else {
                        currentConversations.add(0, updatedConversation)
                    }

                    val sortedConversations = currentConversations.distinctBy { it.addresseeName }
                        .sortedByDescending { it.lastMessageTime }

                    _conversations.value = sortedConversations
                    _conversationsState.value = Resource.Success(sortedConversations)
                }
        }
    }

    fun updateUserProfileInConversations(
        oldUsername: String?,
        newUsername: String,
        newProfileImageUrl: String? = null,
        newLocalImagePath: String? = null,
        newImageRes: Int = 0
    ) {
        viewModelScope.launch {
            try {
                val result = conversationSummaryRepository.updateUserProfileInConversations(
                    oldUsername = oldUsername,
                    newUsername = newUsername,
                    newProfileImageUrl = newProfileImageUrl,
                    newLocalImagePath = newLocalImagePath,
                    newImageRes = newImageRes
                )

                result.onSuccess {
                    loadMoreConversations()
                }
            } catch (_: Exception) {
            }
        }
    }

    fun updateConversationSummary(
        otherUserId: String, lastMessage: String
    ) {
        viewModelScope.launch {
            conversationSummaryRepository.updateConversationSummary(
                userId1 = currentUserId, userId2 = otherUserId, lastMessage = lastMessage
            )
        }
    }

    companion object {
        fun create(
            conversationSummaryRepository: ConversationSummaryRepository, currentUserId: String
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ConversationSummaryViewModel(
                        conversationSummaryRepository, currentUserId
                    ) as T
                }
            }
        }
    }
}