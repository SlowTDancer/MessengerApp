package com.ikhut.messengerapp.data.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.domain.model.ConversationSummary
import com.ikhut.messengerapp.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId

class FirebaseConversationSummaryDataSource {
    private val conversationsDB =
        FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CONVERSATIONS)

    suspend fun updateConversationSummary(
        user1: User, user2: User, lastMessage: String
    ): Result<Unit> {
        return try {
            val conversationForUser1 = ConversationSummary(
                addresseeName = user2.username,
                lastMessage = lastMessage,
                imageRes = user2.imageRes,
                imageUrl = user2.imageUrl,
                localImagePath = user2.localImagePath
            )

            val conversationForUser2 = ConversationSummary(
                addresseeName = user1.username,
                lastMessage = lastMessage,
                imageRes = user1.imageRes,
                imageUrl = user1.imageUrl,
                localImagePath = user1.localImagePath
            )

            val updates = mapOf(
                "${user1.username}/${user2.username}" to conversationForUser1,
                "${user2.username}/${user1.username}" to conversationForUser2
            )

            conversationsDB.updateChildren(updates).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfileInConversations(
        oldUsername: String?,
        newUsername: String,
        profileImageUrl: String? = null,
        localImagePath: String? = null,
        imageRes: Int = 0
    ): Result<Unit> {
        return try {
            if (oldUsername.isNullOrEmpty() || oldUsername == newUsername) {
                return updateUserProfileInfoOnly(
                    newUsername, profileImageUrl, localImagePath, imageRes
                )
            }

            val allConversationsSnapshot = conversationsDB.get().await()
            val updatesToApply = mutableMapOf<String, Any>()
            val pathsToDelete = mutableListOf<String>()

            allConversationsSnapshot.children.forEach { userNode ->
                val userId = userNode.key ?: return@forEach

                userNode.children.forEach { conversationNode ->
                    val conversation = conversationNode.getValue(ConversationSummary::class.java)

                    if (conversation?.addresseeName == oldUsername) {
                        val updatedConversation = conversation.copy(
                            addresseeName = newUsername,
                            imageUrl = profileImageUrl,
                            localImagePath = localImagePath,
                            imageRes = imageRes
                        )

                        updatesToApply["$userId/$newUsername"] = updatedConversation
                        pathsToDelete.add("$userId/$oldUsername")
                    }
                }
            }

            val userOwnConversationsSnapshot = conversationsDB.child(oldUsername).get().await()

            if (userOwnConversationsSnapshot.exists()) {
                userOwnConversationsSnapshot.children.forEach { conversationNode ->
                    val addresseeName = conversationNode.key ?: return@forEach
                    val conversation = conversationNode.getValue(ConversationSummary::class.java)
                    if (conversation != null) {
                        updatesToApply["$newUsername/$addresseeName"] = conversation
                    }
                }
                pathsToDelete.add(oldUsername)
            }

            if (updatesToApply.isNotEmpty()) {
                conversationsDB.updateChildren(updatesToApply).await()
            }

            pathsToDelete.forEach { path ->
                try {
                    conversationsDB.child(path).removeValue().await()
                } catch (_: Exception) {
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun updateUserProfileInfoOnly(
        username: String,
        profileImageUrl: String? = null,
        localImagePath: String? = null,
        imageRes: Int = 0
    ): Result<Unit> {
        return try {
            val allConversationsSnapshot = conversationsDB.get().await()
            val updatesToApply = mutableMapOf<String, Any>()

            allConversationsSnapshot.children.forEach { userNode ->
                val userId = userNode.key ?: return@forEach

                userNode.children.forEach { conversationNode ->
                    val conversation = conversationNode.getValue(ConversationSummary::class.java)

                    if (conversation?.addresseeName == username) {
                        val updatedConversation = conversation.copy(
                            imageUrl = profileImageUrl,
                            localImagePath = localImagePath,
                            imageRes = imageRes
                        )

                        updatesToApply["$userId/$username"] = updatedConversation
                    }
                }
            }

            if (updatesToApply.isNotEmpty()) {
                conversationsDB.updateChildren(updatesToApply).await()
            }

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