package com.ikhut.messengerapp.presentation.homeFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ikhut.messengerapp.databinding.FragmentSettingsBinding
import com.ikhut.messengerapp.presentation.activity.MainActivity
import com.ikhut.messengerapp.presentation.application.getUserRepository
import com.ikhut.messengerapp.presentation.application.getUserSessionManager
import com.ikhut.messengerapp.presentation.viewmodel.SettingsViewModel
import com.ikhut.messengerapp.utils.Resource

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        setupClickListeners()
        observeViewModel()
    }

    private fun initViewModel() {
        val userRepository = getUserRepository()
        val userSessionManager = getUserSessionManager()

        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModel.create(userRepository, userSessionManager)
        )[SettingsViewModel::class.java]
    }

    private fun setupClickListeners() {
        binding.updateButton.setOnClickListener {
            handleUpdateProfile()
        }

        binding.signOutButton.setOnClickListener {
            handleSignOut()
        }

        binding.profileImage.setOnClickListener {
            handleProfileImageClick()
        }
    }

    private fun observeViewModel() {
        settingsViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.updateNicknameEdit.setText(it.username)
                binding.updateJobEdit.setText(it.job)
            }
        }

        settingsViewModel.updateProfileState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.updateButton.isEnabled = false
                    showToast("Updating profile...")
                }
                is Resource.Success -> {
                    binding.updateButton.isEnabled = true
                    showToast("Profile updated successfully!")

                    binding.updateNicknameEdit.error = null
                    binding.updateJobEdit.error = null
                }
                is Resource.Error -> {
                    binding.updateButton.isEnabled = true

                    when {
                        state.message?.contains("Nickname") == true -> {
                            binding.updateNicknameEdit.error = state.message
                        }
                        state.message?.contains("Job") == true -> {
                            binding.updateJobEdit.error = state.message
                        }
                        else -> {
                            showToast("Update failed: ${state.message}")
                        }
                    }
                }
            }
        }
    }

    private fun handleUpdateProfile() {
        val newNickname = binding.updateNicknameEdit.text.toString()
        val newJob = binding.updateJobEdit.text.toString()

        settingsViewModel.updateProfile(newNickname, newJob)
    }

    private fun showSignOutConfirmationDialog() {
        AlertDialog.Builder(requireContext()).setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ -> performSignOut() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun handleSignOut() {
        showSignOutConfirmationDialog()
    }

    private fun performSignOut() {
        settingsViewModel.signOut()

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun handleProfileImageClick() {
        // TODO
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}