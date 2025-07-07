package com.ikhut.messengerapp.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ikhut.messengerapp.data.firebase.FirebaseUserDataSource
import com.ikhut.messengerapp.data.repository.UserRepositoryImpl
import com.ikhut.messengerapp.databinding.ActivityMainBinding
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.domain.repository.UserRepository
import com.ikhut.messengerapp.domain.usecase.LoginUserUseCase
import com.ikhut.messengerapp.domain.usecase.RegisterUserUseCase
import com.ikhut.messengerapp.presentation.authentification.LoginFragment
import com.ikhut.messengerapp.presentation.viewmodel.AuthViewModel
import com.ikhut.messengerapp.utils.Resource

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!

    private lateinit var firebaseUserDataSource: FirebaseUserDataSource
    private lateinit var userRepository: UserRepository
    private lateinit var registerUserUseCase: RegisterUserUseCase
    private lateinit var loginUserUseCase: LoginUserUseCase
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVariables()
        addViewModelListeners()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.mainActivityFragmentContainerView.id, LoginFragment())
                .commit()
        }

    }

    private fun initVariables() {
        firebaseUserDataSource = FirebaseUserDataSource()
        userRepository = UserRepositoryImpl(firebaseUserDataSource)
        registerUserUseCase = RegisterUserUseCase(userRepository)
        loginUserUseCase = LoginUserUseCase(userRepository)
        authViewModel = ViewModelProvider(
            this, AuthViewModel.create(registerUserUseCase, loginUserUseCase)
        )[AuthViewModel::class.java]
    }

    private fun addViewModelListeners() {
        authViewModel.registerState.observe(this) { state ->
            when (state) {
                is Resource.Loading -> showToast("Registering...")
                is Resource.Success -> showToast("Registration successful!")
                is Resource.Error -> showToast("Registration failed: ${state.message}")
            }
        }

        authViewModel.loginState.observe(this) { state ->
            when (state) {
                is Resource.Loading -> showToast("Logging in...")
                is Resource.Success -> {
                    val user = state.data
                    showToast("Login successful! Welcome ${user?.username}")
                }
                is Resource.Error -> showToast("Login failed: ${state.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
