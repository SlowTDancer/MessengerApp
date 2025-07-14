package com.ikhut.messengerapp.domain.repository

import com.ikhut.messengerapp.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun sendMessage(senderId: String, receiverId: String, content: String): Result<String>

    suspend fun getConversation(
        userId1: String, userId2: String, limit: Int = 20, lastMessageTimestamp: Long? = null
    ): Result<List<Message>>

    fun observeNewMessages(
        userId1: String, userId2: String, lastKnownTimestamp: Long = 0
    ): Flow<Message>
}