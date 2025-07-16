package com.ikhut.messengerapp.presentation.activity

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ikhut.messengerapp.R
import com.ikhut.messengerapp.application.getUserRepository
import com.ikhut.messengerapp.application.getUserSessionManager
import com.ikhut.messengerapp.databinding.ActivityChatBinding
import com.ikhut.messengerapp.domain.model.Message
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.presentation.adapters.MessageAdapter
import com.ikhut.messengerapp.presentation.utils.ProfilePictureLoader.loadProfilePicture
import kotlinx.coroutines.launch
import kotlin.math.abs

class ChatActivity : AppCompatActivity() {

    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var messageAdapter: MessageAdapter
    private val targetUsername: String by lazy {
        intent.getStringExtra(EXTRA_USERNAME) ?: ""
    }
    private lateinit var targetUser: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            updatePaddingForKeyboard(imeInsets, systemBarInsets)
            insets
        }

        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.backButton.setOnClickListener { finish() }
        binding.backButtonCollapsed.setOnClickListener { finish() }

        loadUserProfile()
        setupRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updatePaddingForKeyboard(
        imeInsets: androidx.core.graphics.Insets,
        systemBarInsets: androidx.core.graphics.Insets
    ) {
        val padding = resources.getDimensionPixelSize(R.dimen.message_input_container_padding)
        val bottomPadding = imeInsets.bottom.coerceAtLeast(systemBarInsets.bottom) + padding

        binding.messageInputContainer.setPadding(
            binding.messageInputContainer.paddingLeft,
            binding.messageInputContainer.paddingTop,
            binding.messageInputContainer.paddingRight,
            bottomPadding
        )
    }

    private fun setupRecyclerView() {
        val currentUser = getUserSessionManager().currentUser
            ?: throw IllegalStateException("Current user is null")

        messageAdapter = MessageAdapter(currentUser)

        binding.chatRecyclerView.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
                reverseLayout = false
            }
        }
    }



    private fun loadUserProfile() {
        lifecycleScope.launch {
            try {
                targetUser = getUserRepository().getUser(targetUsername).getOrNull()!!
                updateUserProfileViews(targetUser)

                val currentUser = getUserSessionManager().currentUser!!
                createSampleMessages(currentUser, targetUser)
            } catch (e: Exception) {
                updateUserProfileViews(null)
            }
        }
    }

    private fun updateUserProfileViews(user: User?) {
        binding.appbarChatUsername.text = targetUsername
        binding.appbarChatUsernameCollapsed.text = targetUsername

        if (user != null) {
            binding.appbarChatJob.text = user.job
            binding.appbarChatJobCollapsed.text = user.job

            loadUserProfilePictures(user)
        } else {
            binding.appbarChatJob.text = ""
            binding.appbarChatJobCollapsed.text = ""

            binding.profilePicture.setImageResource(R.drawable.avatar_image_placeholder)
            binding.profilePictureCollapsed.setImageResource(R.drawable.avatar_image_placeholder)
        }
    }

    private fun loadUserProfilePictures(user: User) {
        val context = binding.root.context

        loadProfilePicture(context, binding.profilePicture, user.imageRes, user.imageUrl, user.localImagePath, user.username)
        loadProfilePicture(context, binding.profilePictureCollapsed, user.imageRes, user.imageUrl, user.localImagePath, user.username)
    }

    private fun createSampleMessages(currentUser: User, chatMember: User) {
        val baseTime = System.currentTimeMillis() - 3600000 // 1 hour ago

        val list = listOf(
            Message(
                id = "1",
                senderId = chatMember.username,
                receiverId = currentUser.username,
                content = "Hey! How are you doing?",
                timestamp = baseTime
            ),
            Message(
                id = "2",
                senderId = currentUser.username,
                receiverId = chatMember.username,
                content = "Hi! I'm doing great, thanks!",
                timestamp = baseTime + 120000
            ),
            Message(
                id = "3",
                senderId = currentUser.username,
                receiverId = chatMember.username,
                content = "Just finished work, what about you?",
                timestamp = baseTime + 180000
            ),
            Message(
                id = "4",
                senderId = chatMember.username,
                receiverId = currentUser.username,
                content = "That's awesome! I'm still working on a project",
                timestamp = baseTime + 300000
            ),
            Message(
                id = "5",
                senderId = chatMember.username,
                receiverId = currentUser.username,
                content = "Should be done soon though 🎉",
                timestamp = baseTime + 330000
            ),
            Message(
                id = "6",
                senderId = currentUser.username,
                receiverId = chatMember.username,
                content = "Nice! Want to grab coffee later?",
                timestamp = baseTime + 600000
            ),
            Message(
                id = "7",
                senderId = chatMember.username,
                receiverId = currentUser.username,
                content = "Absolutely! I'd love to ☕",
                timestamp = baseTime + 720000
            ),
            Message(
                id = "8",
                senderId = currentUser.username,
                receiverId = chatMember.username,
                content = "Great! There's a place nearby that's really nice.",
                timestamp = baseTime + 780000
            ),
            Message(
                id = "9",
                senderId = chatMember.username,
                receiverId = currentUser.username,
                content = "Cool, send me the location when you're free.",
                timestamp = baseTime + 810000
            ),
            Message(
                id = "10",
                senderId = currentUser.username,
                receiverId = chatMember.username,
                content = "Will do. What time are you done with work?",
                timestamp = baseTime + 840000
            ),
            Message(
                id = "11",
                senderId = chatMember.username,
                receiverId = currentUser.username,
                content = "Around 6 PM. Does that work for you?",
                timestamp = baseTime + 870000
            ),
            Message(
                id = "12",
                senderId = currentUser.username,
                receiverId = chatMember.username,
                content = "Perfect! Let's meet at 6:30 then.",
                timestamp = baseTime + 900000
            ),
            Message(
                id = "13",
                senderId = chatMember.username,
                receiverId = currentUser.username,
                content = "Awesome. Looking forward to it!",
                timestamp = baseTime + 930000
            ),
            Message(
                id = "14",
                senderId = currentUser.username,
                receiverId = chatMember.username,
                content = "Same here 😊",
                timestamp = baseTime + 960000
            ),
            Message(
                id = "15",
                senderId = chatMember.username,
                receiverId = currentUser.username,
                content = "By the way, did you see the new episode of that show?",
                timestamp = baseTime + 990000
            ),
            Message(
                id = "16",
                senderId = currentUser.username,
                receiverId = chatMember.username,
                content = "Not yet! No spoilers 😅",
                timestamp = baseTime + 1020000
            ),
            Message(
                id = "17",
                senderId = chatMember.username,
                receiverId = currentUser.username,
                content = "Haha okay, I won't spoil it. But it's really good!",
                timestamp = baseTime + 1050000
            ),
            Message(
                id = "18",
                senderId = currentUser.username,
                receiverId = chatMember.username,
                content = "Can't wait to watch it tonight.",
                timestamp = baseTime + 1080000
            ),
            Message(
                id = "19",
                senderId = chatMember.username,
                receiverId = currentUser.username,
                content = "Let's talk about it over coffee! ☕🍿",
                timestamp = baseTime + 1110000
            ),
            Message(
                id = "20",
                senderId = currentUser.username,
                receiverId = chatMember.username,
                content = "Deal!",
                timestamp = baseTime + 1140000
            )
        )

        messageAdapter.submitList(list)
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }
}