package com.ikhut.messengerapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.domain.common.Resource
import com.ikhut.messengerapp.domain.model.Message
import com.ikhut.messengerapp.domain.repository.MessageRepository
import kotlinx.coroutines.launch

class ChatViewModel(
    private val messageRepository: MessageRepository,
    private val currentUserId: String,
    private val otherUserId: String
) : ViewModel() {

    private val _sendMessageState = MutableLiveData<Resource<String>>()
    val sendMessageState: LiveData<Resource<String>> = _sendMessageState

    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> = _messages

    private val _messageLoadState = MutableLiveData<Resource<List<Message>>>()
    val messageLoadState: LiveData<Resource<List<Message>>> = _messageLoadState

    private var isLoadingMore = false
    private var allMessagesLoaded = false
    private var lastLoadedTimestamp: Long? = null

    init {
        loadInitialMessages()
        observeNewMessages()
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        _sendMessageState.value = Resource.Loading()

        viewModelScope.launch {
            val result = messageRepository.sendMessage(currentUserId, otherUserId, content)
            _sendMessageState.value = result.fold(onSuccess = { Resource.Success(it) },
                onFailure = { Resource.Error(it.message ?: Constants.ERROR_UNKNOWN) })
        }
    }

    private fun loadInitialMessages() {
        _messages.value = emptyList()
        lastLoadedTimestamp = null
        allMessagesLoaded = false
        loadMoreMessages()
    }

    fun loadMoreMessages(limit: Int = Constants.PAGE_SIZE) {
        if (isLoadingMore || allMessagesLoaded) return

        isLoadingMore = true
        _messageLoadState.value = Resource.Loading()

        viewModelScope.launch {
            val result = messageRepository.getConversation(
                currentUserId, otherUserId, limit, lastLoadedTimestamp
            )

            result.fold(onSuccess = { newMessages ->
                if (newMessages.isEmpty()) {
                    allMessagesLoaded = true
                } else {
                    lastLoadedTimestamp = newMessages.last().timestamp
                    val updated =
                        (newMessages + (_messages.value ?: emptyList())).distinctBy { it.id }
                    _messages.value = updated
                }
                _messageLoadState.value = Resource.Success(_messages.value ?: emptyList())
            }, onFailure = {
                _messageLoadState.value = Resource.Error(it.message ?: Constants.ERROR_UNKNOWN)
            })

            isLoadingMore = false
        }
    }

    private fun observeNewMessages() {
        viewModelScope.launch {
            messageRepository.observeNewMessages(currentUserId, otherUserId).collect { newMessage ->
                val current = _messages.value ?: emptyList()
                if (current.none { it.id == newMessage.id }) {
                    _messages.postValue(current + newMessage)
                }
            }
        }
    }

    companion object {
        fun create(
            messageRepository: MessageRepository, currentUserId: String, otherUserId: String
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ChatViewModel(messageRepository, currentUserId, otherUserId) as T
                }
            }
        }
    }
}
