package com.ikhut.messengerapp.data.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.ikhut.messengerapp.domain.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseMessageDataSource {
    private val messagesDB = FirebaseDatabase.getInstance().getReference("messages")

    private val PAGE_SIZE = 20

    private fun generateConversationId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) "${userId1}_${userId2}" else "${userId2}_${userId1}"
    }

    suspend fun sendMessage(senderId: String, receiverId: String, content: String): Result<String> {
        return try {
            val conversationId = generateConversationId(senderId, receiverId)
            val messageRef = messagesDB.child(conversationId).push()

            val message = Message(
                id = messageRef.key ?: "",
                timestamp = System.currentTimeMillis(),
                content = content,
                senderId = senderId,
                receiverId = receiverId
            )

            messageRef.setValue(message).await()
            Result.success(messageRef.key ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getConversation(
        userId1: String, userId2: String, limit: Int = PAGE_SIZE, lastMessageTimestamp: Long? = null
    ): Result<List<Message>> {
        return try {
            val conversationId = generateConversationId(userId1, userId2)

            val query = if (lastMessageTimestamp != null) {
                messagesDB.child(conversationId).orderByChild("timestamp")
                    .endBefore(lastMessageTimestamp.toDouble()).limitToLast(limit)
            } else {
                messagesDB.child(conversationId).orderByChild("timestamp").limitToLast(limit)
            }

            val snapshot = query.get().await()
            val messages = mutableListOf<Message>()

            snapshot.children.forEach { child ->
                child.getValue(Message::class.java)?.let { message ->
                    messages.add(message)
                }
            }

            Result.success(messages.reversed())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeNewMessages(
        userId1: String, userId2: String, lastKnownTimestamp: Long = 0
    ): Flow<Message> {
        return callbackFlow {
            val conversationId = generateConversationId(userId1, userId2)

            val query = messagesDB.child(conversationId).orderByChild("timestamp")
                .startAfter(lastKnownTimestamp.toDouble())

            val listener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.getValue(Message::class.java)?.let { message ->
                        if (message.timestamp > lastKnownTimestamp) {
                            trySend(message)
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }

            query.addChildEventListener(listener)

            awaitClose {
                query.removeEventListener(listener)
            }
        }
    }
}