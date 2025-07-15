package com.ikhut.messengerapp.data.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.domain.model.ConversationSummary
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId

class FirebaseConversationDataSource {
    private val conversationsDB =
        FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CONVERSATIONS)

    private fun generateConversationKey(userId1: String, userId2: String): String {
        return if (userId1 < userId2) "${userId1}_${userId2}" else "${userId2}_${userId1}"
    }

    suspend fun updateConversationSummary(
        userId1: String, userId2: String, lastMessage: String, lastMessageTime: LocalDateTime
    ): Result<Unit> {
        return try {
            val conversationKey = generateConversationKey(userId1, userId2)

            val conversationForUser1 = ConversationSummary(
                addresseeName = userId2,
                lastMessageTime = lastMessageTime,
                lastMessage = lastMessage,
                profileImageUrl = null,
                profileImageRes = null
            )

            val conversationForUser2 = ConversationSummary(
                addresseeName = userId1,
                lastMessageTime = lastMessageTime,
                lastMessage = lastMessage,
                profileImageUrl = null,
                profileImageRes = null
            )

            val updates = mapOf(
                "${userId1}/$conversationKey" to conversationForUser1,
                "${userId2}/$conversationKey" to conversationForUser2
            )

            conversationsDB.updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentConversations(
        userId: String,
        limit: Int = Constants.PAGE_SIZE,
        lastConversationTime: LocalDateTime? = null
    ): Result<List<ConversationSummary>> {
        return try {
            val userConversationsRef = conversationsDB.child(userId)

            val query = if (lastConversationTime != null) {
                val timestamp =
                    lastConversationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                userConversationsRef.orderByChild(Constants.PARAM_LAST_MESSAGE_TIME)
                    .endBefore(timestamp.toDouble()).limitToLast(limit)
            } else {
                userConversationsRef.orderByChild(Constants.PARAM_LAST_MESSAGE_TIME)
                    .limitToLast(limit)
            }

            val snapshot = query.get().await()
            val conversations = mutableListOf<ConversationSummary>()

            snapshot.children.forEach { child ->
                child.getValue(ConversationSummary::class.java)?.let { conversation ->
                    conversations.add(conversation)
                }
            }

            Result.success(conversations.reversed())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeConversationUpdates(userId: String): Flow<ConversationSummary> {
        return callbackFlow {
            val userConversationsRef = conversationsDB.child(userId)

            val listener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.getValue(ConversationSummary::class.java)?.let { conversation ->
                        trySend(conversation)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.getValue(ConversationSummary::class.java)?.let { conversation ->
                        trySend(conversation)
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }

            userConversationsRef.addChildEventListener(listener)

            awaitClose {
                userConversationsRef.removeEventListener(listener)
            }
        }
    }
}