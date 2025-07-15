package com.ikhut.messengerapp.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.ikhut.messengerapp.R
import com.ikhut.messengerapp.application.getUserRepository
import com.ikhut.messengerapp.databinding.ActivityChatBinding
import com.ikhut.messengerapp.presentation.utils.ProfilePictureLoader.loadProfilePicture
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.messageInputContainer.setPadding(
                16,
                16,
                16,
                imeInsets.bottom.coerceAtLeast(systemBarInsets.bottom) + 16
            )

            // Consume insets
            WindowInsetsCompat.CONSUMED
        }

        setupViews()

        val username = intent.getStringExtra(EXTRA_USERNAME) ?: ""

        lifecycleScope.launch {
            val result = getUserRepository().getUser(username)
            val user = result.getOrNull()

            binding.appbarChatUsername.text = username
            if (user != null) {
                binding.appbarChatJob.text = user.job
                loadProfilePicture(binding.root.context, binding.profilePicture, user.imageRes, user.imageUrl, user.localImagePath, user.username)
            } else {
                binding.appbarChatJob.text = ""
                binding.profilePicture.setImageResource(R.drawable.avatar_image_placeholder)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupViews() {
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }
}