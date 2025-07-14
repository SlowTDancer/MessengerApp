package com.ikhut.messengerapp.application

import android.app.Application
import com.ikhut.messengerapp.data.firebase.FirebaseMessageDataSource
import com.ikhut.messengerapp.data.firebase.FirebaseUserDataSource
import com.ikhut.messengerapp.data.repository.MessageRepositoryImpl
import com.ikhut.messengerapp.data.repository.UserRepositoryImpl
import com.ikhut.messengerapp.data.session.UserSessionManager
import com.ikhut.messengerapp.domain.repository.MessageRepository
import com.ikhut.messengerapp.domain.repository.UserRepository

class MessengerApplication : Application() {

    val userRepository: UserRepository by lazy {
        val firebaseUserDataSource = FirebaseUserDataSource()
        UserRepositoryImpl(firebaseUserDataSource)
    }

    val messageRepository: MessageRepository by lazy {
        val firebaseMessageDataSource = FirebaseMessageDataSource()
        MessageRepositoryImpl(firebaseMessageDataSource)
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

fun getUserRepository(): UserRepository {
    return MessengerApplication.instance.userRepository
}

fun getMessageRepository(): MessageRepository {
    return MessengerApplication.instance.messageRepository
}

fun getUserSessionManager(): UserSessionManager {
    return MessengerApplication.instance.userSessionManager
}