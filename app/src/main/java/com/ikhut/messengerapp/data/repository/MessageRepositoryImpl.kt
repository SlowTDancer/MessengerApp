package com.ikhut.messengerapp.data.repository

import com.ikhut.messengerapp.data.firebase.FirebaseMessageDataSource
import com.ikhut.messengerapp.domain.model.Message
import com.ikhut.messengerapp.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

class MessageRepositoryImpl(
    private val dataSource: FirebaseMessageDataSource
) : MessageRepository {

    override suspend fun sendMessage(
        senderId: String, receiverId: String, content: String
    ): Result<String> {
        return dataSource.sendMessage(senderId, receiverId, content)
    }

    override suspend fun getConversation(
        userId1: String, userId2: String, limit: Int, lastMessageTimestamp: Long?
    ): Result<List<Message>> {
        return dataSource.getConversation(userId1, userId2, limit, lastMessageTimestamp)
    }

    override fun observeNewMessages(
        userId1: String, userId2: String, lastKnownTimestamp: Long
    ): Flow<Message> {
        return dataSource.observeNewMessages(userId1, userId2, lastKnownTimestamp)
    }
}