package com.ikhut.messengerapp.data.repository

import com.ikhut.messengerapp.data.firebase.FirebaseConversationDataSource
import com.ikhut.messengerapp.domain.model.ConversationSummary
import com.ikhut.messengerapp.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class ConversationRepositoryImpl(
    private val dataSource: FirebaseConversationDataSource
) : ConversationRepository {

    override suspend fun updateConversationSummary(
        userId1: String, userId2: String, lastMessage: String, lastMessageTime: LocalDateTime
    ): Result<Unit> {
        return dataSource.updateConversationSummary(userId1, userId2, lastMessage, lastMessageTime)
    }

    override suspend fun getRecentConversations(
        userId: String, limit: Int, lastConversationTime: LocalDateTime?
    ): Result<List<ConversationSummary>> {
        return dataSource.getRecentConversations(userId, limit, lastConversationTime)
    }

    override fun observeConversationUpdates(userId: String): Flow<ConversationSummary> {
        return dataSource.observeConversationUpdates(userId)
    }
}