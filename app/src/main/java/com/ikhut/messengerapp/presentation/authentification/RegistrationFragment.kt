package com.ikhut.messengerapp.presentation.authentification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ikhut.messengerapp.application.getUserSessionManager
import com.ikhut.messengerapp.data.session.UserSessionManager
import com.ikhut.messengerapp.databinding.FragmentRegistrationBinding
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.presentation.viewmodel.AuthViewModel

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var userSessionManager: UserSessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        userSessionManager = getUserSessionManager()


        binding.signUpButton.setOnClickListener {
            val nickname = binding.authentificationNicknameInputEditLayout.text.toString()
            val password = binding.authentificationPasswordInputEditLayout.text.toString()
            val job = binding.authentificationWhatIDoInputEditLayout.text.toString()
            val newUser = User(nickname, job, password)

            authViewModel.registerUser(newUser)
            userSessionManager.loginUser(newUser)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}