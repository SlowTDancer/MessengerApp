package com.ikhut.messengerapp.domain.repository

import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.domain.model.ConversationSummary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ConversationRepository {
    suspend fun updateConversationSummary(
        userId1: String, userId2: String, lastMessage: String
    ): Result<Unit>

    suspend fun getRecentConversations(
        userId: String,
        limit: Int = Constants.PAGE_SIZE,
        lastConversationTime: LocalDateTime? = null
    ): Result<List<ConversationSummary>>

    fun observeConversationUpdates(userId: String): Flow<ConversationSummary>
}