package com.ikhut.messengerapp.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ikhut.messengerapp.application.getUserRepository
import com.ikhut.messengerapp.application.getUserSessionManager
import com.ikhut.messengerapp.data.session.UserSessionManager
import com.ikhut.messengerapp.databinding.ActivityMainBinding
import com.ikhut.messengerapp.domain.common.Resource
import com.ikhut.messengerapp.domain.repository.UserRepository
import com.ikhut.messengerapp.domain.usecase.LoginUserUseCase
import com.ikhut.messengerapp.domain.usecase.RegisterUserUseCase
import com.ikhut.messengerapp.presentation.authentification.LoginFragment
import com.ikhut.messengerapp.presentation.components.LoadingOverlay
import com.ikhut.messengerapp.presentation.viewmodel.AuthViewModel


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!

    private val userSessionManager: UserSessionManager = getUserSessionManager()
    private val userRepository: UserRepository = getUserRepository()
    private lateinit var loadingOverlay: LoadingOverlay
    private lateinit var registerUserUseCase: RegisterUserUseCase
    private lateinit var loginUserUseCase: LoginUserUseCase
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVariables()
        initComponents()
        addViewModelListeners()
        initApp()
    }


    private fun initVariables() {
        registerUserUseCase = RegisterUserUseCase(userRepository)
        loginUserUseCase = LoginUserUseCase(userRepository)
        authViewModel = ViewModelProvider(
            this, AuthViewModel.create(registerUserUseCase, loginUserUseCase)
        )[AuthViewModel::class.java]
    }

    private fun initComponents() {
        loadingOverlay = LoadingOverlay(this)
    }

    private fun addViewModelListeners() {
        authViewModel.registerState.observe(this) { state ->
            when (state) {
                is Resource.Loading -> loadingOverlay.show()
                is Resource.Success -> {
                    loadingOverlay.dismiss()
                    navigateToHome()
                }

                is Resource.Error -> {
                    loadingOverlay.dismiss()
                    showToast("Registration failed: ${state.message}")
                }
            }
        }

        authViewModel.loginState.observe(this) { state ->
            when (state) {
                is Resource.Loading -> loadingOverlay.show()
                is Resource.Success -> {
                    loadingOverlay.dismiss()
                    val user = state.data
                    if (user != null) {
                        userSessionManager.loginUser(user)
                        navigateToHome()
                    }
                }

                is Resource.Error -> {
                    loadingOverlay.dismiss()
                    showToast("Login failed: ${state.message}")
                }
            }
        }
    }

    private fun initApp() {
        if (userSessionManager.isLoggedIn) {
            val username = userSessionManager.getCurrentUsername()
            showToast("Login successful! Welcome $username")
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(binding.mainActivityFragmentContainerView.id, LoginFragment()).commit()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
