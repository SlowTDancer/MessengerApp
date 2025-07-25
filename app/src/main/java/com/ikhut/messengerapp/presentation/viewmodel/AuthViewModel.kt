package com.ikhut.messengerapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.domain.common.Resource
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.usecase.LoginUserUseCase
import com.ikhut.messengerapp.domain.usecase.RegisterUserUseCase
import kotlinx.coroutines.launch

class AuthViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {
    private val _registerState = MutableLiveData<Resource<Unit>>()
    val registerState: LiveData<Resource<Unit>> = _registerState

    private val _loginState = MutableLiveData<Resource<User>>()
    val loginState: LiveData<Resource<User>> = _loginState

    fun registerUser(user: User) {
        _registerState.value = Resource.Loading()

        viewModelScope.launch {
            val result = registerUserUseCase(user)
            _registerState.value = result.fold(onSuccess = { Resource.Success(Unit) },
                onFailure = { Resource.Error(it.message ?: Constants.ERROR_UNKNOWN) })
        }
    }

    fun loginUser(username: String, password: String) {
        _loginState.value = Resource.Loading()

        viewModelScope.launch {
            val result = loginUserUseCase(username, password)
            _loginState.value = result.fold(onSuccess = { user -> Resource.Success(user) },
                onFailure = { error -> Resource.Error(error.message ?: Constants.ERROR_UNKNOWN) })
        }
    }

    companion object {
        fun create(
            registerUserUseCase: RegisterUserUseCase, loginUserUseCase: LoginUserUseCase
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(registerUserUseCase, loginUserUseCase) as T
                }
            }
        }
    }
}
