package com.ikhut.messengerapp.presentation.homeFragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.ikhut.messengerapp.databinding.FragmentSettingsBinding
import com.ikhut.messengerapp.presentation.MainActivity

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        binding.updateNicknameEdit.setText("Sayed Eftiaz")
        binding.updateJobEdit.setText("Manager")
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

    private fun handleUpdateProfile() {
        val nickname = binding.updateNicknameEdit.text.toString().trim()
        val job = binding.updateJobEdit.text.toString().trim()

        if (nickname.isEmpty()) {
            binding.updateNicknameEdit.error = "Nickname cannot be empty"
            return
        }

        if (job.isEmpty()) {
            binding.updateJobEdit.error = "Job cannot be empty"
            return
        }

        binding.updateNicknameEdit.error = null
        binding.updateJobEdit.error = null
    }

    private fun showSignOutConfirmationDialog() {
        AlertDialog.Builder(requireContext()).setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ -> performSignOut() }.setNegativeButton("No", null)
            .show()
    }

    private fun handleSignOut() {
        showSignOutConfirmationDialog()
    }

    private fun performSignOut() {
        val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun handleProfileImageClick() {
        
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}