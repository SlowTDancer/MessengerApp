package com.ikhut.messengerapp.presentation.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import com.ikhut.messengerapp.R
import com.ikhut.messengerapp.application.getUserRepository
import com.ikhut.messengerapp.databinding.ActivityChatBinding
import com.ikhut.messengerapp.presentation.utils.ProfilePictureLoader.loadProfilePicture
import kotlinx.coroutines.launch
import kotlin.math.abs

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

            // Get dimension values properly
            val padding = resources.getDimensionPixelSize(R.dimen.message_input_container_padding)
            val bottomPadding = imeInsets.bottom.coerceAtLeast(systemBarInsets.bottom) + padding

            // Apply padding only to the bottom
            binding.messageInputContainer.setPadding(
                binding.messageInputContainer.paddingLeft,
                binding.messageInputContainer.paddingTop,
                binding.messageInputContainer.paddingRight,
                bottomPadding
            )

            // Update RecyclerView padding to prevent messages from being hidden
            binding.chatRecyclerView.setPadding(
                binding.chatRecyclerView.paddingLeft,
                binding.chatRecyclerView.paddingTop,
                binding.chatRecyclerView.paddingRight,
                bottomPadding + binding.messageInputContainer.height
            )

            // Return unconsumed insets for other views
            insets
        }

        setupViews()
        setupCollapsingToolbar()

        val username = intent.getStringExtra(EXTRA_USERNAME) ?: ""

        lifecycleScope.launch {
            val result = getUserRepository().getUser(username)
            val user = result.getOrNull()

            binding.appbarChatUsername.text = username
            binding.appbarChatUsernameCollapsed.text = username
            if (user != null) {
                binding.appbarChatJob.text = user.job
                binding.appbarChatJobCollapsed.text = user.job
                loadProfilePicture(binding.root.context, binding.profilePicture, user.imageRes, user.imageUrl, user.localImagePath, user.username)
                loadProfilePicture(binding.root.context, binding.profilePictureCollapsed, user.imageRes, user.imageUrl, user.localImagePath, user.username)
            } else {
                binding.appbarChatJob.text = ""
                binding.appbarChatJobCollapsed.text = ""
                binding.profilePicture.setImageResource(R.drawable.avatar_image_placeholder)
                binding.profilePictureCollapsed.setImageResource(R.drawable.avatar_image_placeholder)
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

        binding.backButtonCollapsed.setOnClickListener {
            finish()
        }
    }

    // In your Activity/Fragment
    private fun setupCollapsingToolbar() {
        val appBarLayout = findViewById<AppBarLayout>(R.id.app_bar_layout)
        val expandedLayout = findViewById<View>(R.id.expanded_layout)
        val collapsedLayout = findViewById<View>(R.id.collapsed_layout)

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / totalScrollRange.toFloat()

            // Fade between layouts
            expandedLayout.alpha = 1f - percentage
            collapsedLayout.alpha = percentage

            // Show/hide layouts based on collapse state
            if (percentage > 0.5f) {
                collapsedLayout.visibility = View.VISIBLE
                expandedLayout.visibility = View.INVISIBLE
            } else {
                collapsedLayout.visibility = View.INVISIBLE
                expandedLayout.visibility = View.VISIBLE
            }
        })
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }
}