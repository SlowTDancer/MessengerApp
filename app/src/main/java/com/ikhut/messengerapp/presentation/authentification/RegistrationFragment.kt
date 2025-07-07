package com.ikhut.messengerapp.presentation.authentification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ikhut.messengerapp.databinding.FragmentRegistrationBinding
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.presentation.viewmodel.AuthViewModel

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        binding.signUpButton.setOnClickListener {
            val nickname = binding.authentificationNicknameInputEditLayout.text.toString()
            val password = binding.authentificationPasswordInputEditLayout.text.toString()
            val job = binding.authentificationWhatIDoInputEditLayout.text.toString()

            authViewModel.registerUser(User(nickname, job, password))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}