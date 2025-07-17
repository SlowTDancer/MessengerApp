package com.ikhut.messengerapp.data.repository

import com.ikhut.messengerapp.data.firebase.FirebaseConversationSummaryDataSource
import com.ikhut.messengerapp.domain.model.ConversationSummary
import com.ikhut.messengerapp.domain.model.UpdateType
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.ConversationSummaryRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class ConversationSummaryRepositoryImpl(
    private val dataSource: FirebaseConversationSummaryDataSource
) : ConversationSummaryRepository {

    override suspend fun updateConversationSummary(
        user1: User, user2: User, lastMessage: String
    ): Result<Unit> {
        return dataSource.updateConversationSummary(user1, user2, lastMessage)
    }

    override suspend fun getRecentConversationSummaries(
        userId: String, limit: Int, lastConversationTime: LocalDateTime?
    ): Result<List<ConversationSummary>> {
        return dataSource.getRecentConversations(userId, limit, lastConversationTime)
    }

    override suspend fun updateUserProfileInConversations(
        oldUsername: String?,
        newUsername: String,
        newProfileImageUrl: String?,
        newLocalImagePath: String?,
        newImageRes: Int
    ): Result<Unit> {
        return try {
            dataSource.updateUserProfileInConversations(
                oldUsername = oldUsername,
                newUsername = newUsername,
                profileImageUrl = newProfileImageUrl,
                localImagePath = newLocalImagePath,
                imageRes = newImageRes
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeConversationUpdates(userId: String): Flow<Pair<ConversationSummary, UpdateType>> {
        return dataSource.observeConversationUpdates(userId)
    }
}