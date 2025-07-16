package com.ikhut.messengerapp.presentation.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ikhut.messengerapp.R
import com.ikhut.messengerapp.application.getConversationSummaryRepository
import com.ikhut.messengerapp.application.getUserRepository
import com.ikhut.messengerapp.application.getUserSessionManager
import com.ikhut.messengerapp.application.getMessageRepository
import com.ikhut.messengerapp.databinding.ActivityChatBinding
import com.ikhut.messengerapp.domain.common.Resource
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.presentation.adapters.MessageAdapter
import com.ikhut.messengerapp.presentation.utils.ProfilePictureLoader.loadProfilePicture
import com.ikhut.messengerapp.presentation.viewmodel.ChatViewModel
import com.ikhut.messengerapp.presentation.viewmodel.ConversationSummaryViewModel
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var conversationSummaryViewModel: ConversationSummaryViewModel

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
        setupViewModel()
        setupMessageInput()
        observeViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupViewModel() {
        val currentUser = getUserSessionManager().currentUser
            ?: throw IllegalStateException("Current user is null")

        chatViewModel = ViewModelProvider(
            this,
            ChatViewModel.create(getMessageRepository(), currentUser.username, targetUsername)
        )[ChatViewModel::class.java]

        conversationSummaryViewModel = ViewModelProvider(
            this,
            ConversationSummaryViewModel.create(getConversationSummaryRepository(), currentUser.username)
        )[ConversationSummaryViewModel::class.java]
    }

    private fun setupMessageInput() {
        binding.sendButton.setOnClickListener {
            val messageContent = binding.messageInput.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                conversationSummaryViewModel.updateConversationSummary(targetUser.username, messageContent)
                chatViewModel.sendMessage(messageContent)
                binding.messageInput.setText("")
            }
        }
    }

    private fun observeViewModel() {
        chatViewModel.sendMessageState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> { binding.sendButton.isEnabled = false }
                is Resource.Success -> {
                    binding.sendButton.isEnabled = true
                    scrollToBottom()
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Failed to send message: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe messages list
        chatViewModel.messages.observe(this) { messages ->
            if (messages == null || messages.isEmpty()) return@observe

            val isAtBottom = isAtBottom()
            messageAdapter.submitList(messages) {
                if (isAtBottom || messageAdapter.itemCount <= 20) {
                    scrollToBottom()
                }
            }
        }

        chatViewModel.messageLoadState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    updateEmptyState(false)
                }
                is Resource.Success -> {
                    // Show empty state only if no messages after successful load
                    val currentMessages = chatViewModel.messages.value
                    updateEmptyState(currentMessages == null || currentMessages.isEmpty())
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Failed to load messages: ${resource.message}", Toast.LENGTH_SHORT).show()
                    val currentMessages = chatViewModel.messages.value
                    updateEmptyState(currentMessages == null || currentMessages.isEmpty())
                }
            }
        }
    }

    private fun scrollToBottom() {
        binding.chatRecyclerView.post {
            if (messageAdapter.itemCount > 0) {
                binding.chatRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
            }
        }
    }

    private fun isAtBottom(): Boolean {
        val layoutManager = binding.chatRecyclerView.layoutManager as LinearLayoutManager
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        val totalItems = messageAdapter.itemCount

        // Consider "at bottom" if within 3 items of the end
        return lastVisiblePosition >= totalItems - 3
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

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Load more messages when scrolling to the top
                    if (firstVisibleItemPosition <= 5) {
                        chatViewModel.loadMoreMessages()
                    }
                }
            })
        }
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            try {
                targetUser = getUserRepository().getUser(targetUsername).getOrNull()!!
                updateUserProfileViews(targetUser)
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

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyStateText.visibility = android.view.View.VISIBLE
            binding.chatRecyclerView.visibility = android.view.View.GONE
        } else {
            binding.emptyStateText.visibility = android.view.View.GONE
            binding.chatRecyclerView.visibility = android.view.View.VISIBLE
        }
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }
}