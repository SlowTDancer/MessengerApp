package com.ikhut.messengerapp.presentation.homeFragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.application.getConversationSummaryRepository
import com.ikhut.messengerapp.application.getUserRepository
import com.ikhut.messengerapp.application.getUserSessionManager
import com.ikhut.messengerapp.databinding.FragmentSettingsBinding
import com.ikhut.messengerapp.domain.common.Resource
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.presentation.activity.MainActivity
import com.ikhut.messengerapp.presentation.components.LoadingOverlay
import com.ikhut.messengerapp.presentation.utils.ProfilePictureLoader
import com.ikhut.messengerapp.presentation.viewmodel.ConversationSummaryViewModel
import com.ikhut.messengerapp.presentation.viewmodel.ImageViewModel
import com.ikhut.messengerapp.presentation.viewmodel.SettingsViewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var imageViewModel: ImageViewModel
    private lateinit var conversationSummaryViewModel: ConversationSummaryViewModel
    private lateinit var loadingOverlay: LoadingOverlay
    private lateinit var user: User
    private var oldUsername: String? = null

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            handleImageSelected(it)
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            showToast(Constants.ERROR_PERMISSION_DENIED)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initProfilePicture()
        initComponents()
        setupClickListeners()
        observeViewModel()
    }

    private fun initViewModel() {
        settingsViewModel = ViewModelProvider(
            this, SettingsViewModel.create(getUserRepository(), getUserSessionManager())
        )[SettingsViewModel::class.java]
        user = settingsViewModel.currentUser.value!!

        conversationSummaryViewModel = ViewModelProvider(
            this,
            ConversationSummaryViewModel.create(getConversationSummaryRepository(), user.username)
        )[ConversationSummaryViewModel::class.java]

        imageViewModel = ViewModelProvider(
            this, ImageViewModel.create()
        )[ImageViewModel::class.java]
    }

    private fun initProfilePicture() {
        settingsViewModel.currentUser.value?.let { user ->
            ProfilePictureLoader.loadProfilePicture(
                context = binding.root.context,
                imageView = binding.profileImage,
                imageRes = user.imageRes,
                imageUrl = user.imageUrl,
                localImagePath = user.localImagePath,
                placeholderName = user.username,
            )
        }
    }

    private fun initComponents() {
        loadingOverlay = LoadingOverlay(requireContext())
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
                    loadingOverlay.show()
                }

                is Resource.Success -> {
                    loadingOverlay.dismiss()
                    binding.updateButton.isEnabled = true
                    showToast(Constants.SUCCESS_PROFILE_UPDATED)

                    binding.updateNicknameEdit.error = null
                    binding.updateJobEdit.error = null

                    updateConversationsAfterProfileChange(state.data!!)
                }

                is Resource.Error -> {
                    loadingOverlay.dismiss()
                    binding.updateButton.isEnabled = true

                    when {
                        state.message?.contains(Constants.PARAM_NICKNAME) == true -> {
                            binding.updateNicknameEdit.error = state.message
                        }

                        state.message?.contains(Constants.PARAM_JOB) == true -> {
                            binding.updateJobEdit.error = state.message
                        }

                        else -> {
                            showToast("Update failed: ${state.message}")
                        }
                    }
                }
            }
        }

        settingsViewModel.updateProfilePictureState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> loadingOverlay.show()

                is Resource.Success -> {
                    loadingOverlay.dismiss()
                    updateConversationsAfterProfileChange(settingsViewModel.currentUser.value!!)

                    initProfilePicture()
                }

                is Resource.Error -> {
                    loadingOverlay.dismiss()
                    showToast("Failed to update profile picture: ${state.message}")
                }
            }
        }

        imageViewModel.imageUpdateState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    loadingOverlay.show()
                }

                is Resource.Success -> {
                    loadingOverlay.dismiss()
                    val newPath = state.data
                    user.localImagePath = newPath
                    ProfilePictureLoader.loadProfilePicture(
                        context = requireContext(),
                        imageView = binding.profileImage,
                        imageRes = user.imageRes,
                        imageUrl = user.imageUrl,
                        localImagePath = newPath,
                        placeholderName = user.username
                    )
                    settingsViewModel.updateUserLocalImagePath(newPath!!)
                }

                is Resource.Error -> {
                    loadingOverlay.dismiss()
                    showToast("Error: ${state.message}")
                }
            }
        }
    }

    private fun updateConversationsAfterProfileChange(updatedUser: User) {
        conversationSummaryViewModel.updateUserProfileInConversations(
            oldUsername = oldUsername!!,
            newUsername = updatedUser.username,
            newProfileImageUrl = updatedUser.imageUrl,
            newLocalImagePath = updatedUser.localImagePath,
            newImageRes = updatedUser.imageRes
        )
    }

    private fun handleUpdateProfile() {
        val newNickname = binding.updateNicknameEdit.text.toString()
        val newJob = binding.updateJobEdit.text.toString()
        oldUsername = settingsViewModel.currentUser.value!!.username
        settingsViewModel.updateProfile(newNickname, newJob)
    }

    private fun showSignOutConfirmationDialog() {
        AlertDialog.Builder(requireContext()).setTitle(Constants.HEADER_SIGN_OUT)
            .setMessage(Constants.QUERY_SIGN_OUT)
            .setPositiveButton(Constants.POSITIVE_RESPONSE_SIGN_OUT) { _, _ -> performSignOut() }
            .setNegativeButton(Constants.NEGATIVE_RESPONSE_SIGN_OUT, null).show()
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
        checkPermissionAndOpenGallery()
    }

    private fun checkPermissionAndOpenGallery() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                openGallery()
            }

            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }

            else -> {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun handleImageSelected(uri: Uri) {
        imageViewModel.handleImageSelected(
            context = requireContext(),
            uri = uri,
            currentLocalPath = user.localImagePath,
            userId = user.username
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}