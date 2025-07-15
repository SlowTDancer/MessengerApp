package com.ikhut.messengerapp.domain.repository

import com.ikhut.messengerapp.domain.model.ConversationSummary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ConversationRepository {
    suspend fun updateConversationSummary(
        userId1: String, userId2: String, lastMessage: String, lastMessageTime: LocalDateTime
    ): Result<Unit>

    suspend fun getRecentConversations(
        userId: String, limit: Int = 20, lastConversationTime: LocalDateTime? = null
    ): Result<List<ConversationSummary>>

    fun observeConversationUpdates(userId: String): Flow<ConversationSummary>
}