package com.ikhut.messengerapp.presentation.authentification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ikhut.messengerapp.databinding.FragmentLoginBinding
import com.ikhut.messengerapp.presentation.MainActivity
import com.ikhut.messengerapp.presentation.viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpButtonInLoginFragment.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            val containerId = mainActivity.binding.mainActivityFragmentContainerView.id

            parentFragmentManager.beginTransaction()
                .replace(containerId, RegistrationFragment())
                .commit()
        }

        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        binding.signInButton.setOnClickListener {
            val nickname = binding.authentificationNicknameInputEditLayout.text.toString()
            val password = binding.authentificationPasswordInputEditLayout.text.toString()
            authViewModel.loginUser(nickname, password)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}