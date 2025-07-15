package com.ikhut.messengerapp.application

import android.app.Application
import com.ikhut.messengerapp.data.firebase.FirebaseConversationSummaryDataSource
import com.ikhut.messengerapp.data.firebase.FirebaseMessageDataSource
import com.ikhut.messengerapp.data.firebase.FirebaseUserDataSource
import com.ikhut.messengerapp.data.repository.ConversationSummaryRepositoryImpl
import com.ikhut.messengerapp.data.repository.MessageRepositoryImpl
import com.ikhut.messengerapp.data.repository.UserRepositoryImpl
import com.ikhut.messengerapp.data.session.UserSessionManager
import com.ikhut.messengerapp.domain.repository.ConversationSummaryRepository
import com.ikhut.messengerapp.domain.repository.MessageRepository
import com.ikhut.messengerapp.domain.repository.UserRepository

class MessengerApplication : Application() {

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(FirebaseUserDataSource())
    }

    val messageRepository: MessageRepository by lazy {
        MessageRepositoryImpl(FirebaseMessageDataSource())
    }

    val conversationSummaryRepository: ConversationSummaryRepository by lazy {
        ConversationSummaryRepositoryImpl(FirebaseConversationSummaryDataSource())
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
fun getConversationSummaryRepository() = MessengerApplication.instance.conversationSummaryRepository
fun getUserSessionManager() = MessengerApplication.instance.userSessionManager
