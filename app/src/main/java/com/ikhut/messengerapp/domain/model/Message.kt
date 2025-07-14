package com.ikhut.messengerapp.domain.model

data class Message(
    val id: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val content: String = "",
    val senderId: String = "",
    val receiverId: String = ""
)