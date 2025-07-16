package com.ikhut.messengerapp.domain.repository

import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.domain.model.ConversationSummary
import com.ikhut.messengerapp.domain.model.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ConversationSummaryRepository {
    suspend fun updateConversationSummary(
        user1: User, user2: User, lastMessage: String
    ): Result<Unit>

    suspend fun getRecentConversationSummaries(
        userId: String,
        limit: Int = Constants.PAGE_SIZE,
        lastConversationTime: LocalDateTime? = null
    ): Result<List<ConversationSummary>>

    suspend fun updateUserProfileInConversations(
        oldUsername: String?,
        newUsername: String,
        newProfileImageUrl: String? = null,
        newLocalImagePath: String? = null,
        newImageRes: Int = 0
    ): Result<Unit>

    fun observeConversationUpdates(userId: String): Flow<ConversationSummary>
}