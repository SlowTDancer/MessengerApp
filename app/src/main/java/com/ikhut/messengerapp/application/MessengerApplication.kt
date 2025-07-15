package com.ikhut.messengerapp.application

import android.app.Application
import com.ikhut.messengerapp.data.firebase.FirebaseConversationDataSource
import com.ikhut.messengerapp.data.firebase.FirebaseMessageDataSource
import com.ikhut.messengerapp.data.firebase.FirebaseUserDataSource
import com.ikhut.messengerapp.data.repository.ConversationRepositoryImpl
import com.ikhut.messengerapp.data.repository.MessageRepositoryImpl
import com.ikhut.messengerapp.data.repository.UserRepositoryImpl
import com.ikhut.messengerapp.data.session.UserSessionManager
import com.ikhut.messengerapp.domain.repository.ConversationRepository
import com.ikhut.messengerapp.domain.repository.MessageRepository
import com.ikhut.messengerapp.domain.repository.UserRepository

class MessengerApplication : Application() {

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(FirebaseUserDataSource())
    }

    val messageRepository: MessageRepository by lazy {
        MessageRepositoryImpl(FirebaseMessageDataSource())
    }

    val conversationRepository: ConversationRepository by lazy {
        ConversationRepositoryImpl(FirebaseConversationDataSource())
    }

    val userSessionManager: UserSessionManager by lazy {
        UserSessionManager(this, userRepository)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MessengerApplication
            private set
    }
}

fun getUserRepository() = MessengerApplication.instance.userRepository
fun getMessageRepository() = MessengerApplication.instance.messageRepository
fun getConversationRepository() = MessengerApplication.instance.conversationRepository
fun getUserSessionManager() = MessengerApplication.instance.userSessionManager
