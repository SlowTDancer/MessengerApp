package com.ikhut.messengerapp.presentation.application

import android.app.Application
import com.ikhut.messengerapp.data.firebase.FirebaseUserDataSource
import com.ikhut.messengerapp.data.repository.UserRepositoryImpl
import com.ikhut.messengerapp.domain.repository.UserRepository
import com.ikhut.messengerapp.presentation.session.UserSessionManager

class MessengerApplication : Application() {

    val userRepository: UserRepository by lazy {
        val firebaseUserDataSource = FirebaseUserDataSource()
        UserRepositoryImpl(firebaseUserDataSource)
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

fun getUserSessionManager(): UserSessionManager {
    return MessengerApplication.instance.userSessionManager
}