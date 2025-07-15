package com.ikhut.messengerapp.presentation.homeFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.ikhut.messengerapp.R
import com.ikhut.messengerapp.databinding.FragmentConversationListBinding
import com.ikhut.messengerapp.domain.model.ConversationSummary
import com.ikhut.messengerapp.presentation.activity.BottomAppBarController
import com.ikhut.messengerapp.presentation.adapters.ConversationSummaryAdapter
import com.ikhut.messengerapp.presentation.components.VerticalSpaceItemDecoration
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.abs

class ConversationListFragment : Fragment() {

    private var _binding: FragmentConversationListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ConversationSummaryAdapter

    private var allConversations: List<ConversationSummary> = emptyList()
    private var filteredConversations: List<ConversationSummary> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConversationListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCollapsingViewAnimation()
        setupRecyclerView()
        setupSearchView()
        loadSampleData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setCollapsingViewAnimation() {
        val appBarLayout = binding.appBarLayout
        val imageView = binding.collapsingToolbarOverlay


        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / totalScrollRange.toFloat()

            // Calculate alpha based on scroll percentage
            // Alpha decreases as we scroll up (percentage increases)
            val alpha = 1.0f - percentage

            // Apply smooth fade animation
            imageView.alpha = alpha

            imageView.visibility = if (alpha <= 0.01f) View.GONE else View.VISIBLE

            (activity as? BottomAppBarController)?.let { controller ->
                if (percentage > 0.5f) {
                    controller.hideBottomAppBar()
                } else {
                    controller.showBottomAppBar()
                }
            }
        })
    }


    private fun setupRecyclerView() {
        adapter = ConversationSummaryAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val spacingInPixels =
            resources.getDimensionPixelSize(R.dimen.conversation_summaries_spacing)
        binding.recyclerView.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterConversations(newText ?: "")
                return true
            }
        })
    }

    private fun filterConversations(query: String) {
        filteredConversations = if (query.isEmpty()) {
            allConversations
        } else {
            allConversations.filter { conversation ->
                conversation.addresseeName.contains(query, ignoreCase = true)
            }
        }
        adapter.submitList(filteredConversations)
    }

    private fun loadSampleData() {
        val sampleConversations = listOf(
            ConversationSummary(
                addresseeName = "Sayed Eftiaz",
                lastMessageTime = LocalDateTime.now().minusMinutes(5).atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                lastMessage = "On my way home but I needed to stop by the block store to get some items",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "John Doe",
                lastMessageTime = LocalDateTime.now().minusMinutes(5).atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                lastMessage = "Hey, how are you doing today?",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Jane Smith",
                lastMessageTime = LocalDateTime.now().minusHours(2).atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                lastMessage = "Can we meet tomorrow for the project discussion?",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Mike Johnson",
                lastMessageTime = LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                lastMessage = "Thanks for the help with the code review!",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Sarah Wilson",
                lastMessageTime = LocalDateTime.now().minusWeeks(1).atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                lastMessage = "This is a very long message to test how the trimming functionality works in the conversation list",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "David Brown",
                lastMessageTime = LocalDateTime.now().minusMonths(1).atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                lastMessage = "See you later!",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Sayed Eftiaz",
                lastMessageTime = LocalDateTime.now().minusMinutes(5).atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                lastMessage = "On my way home but I needed to stop by the block store to...",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "John Doe",
                lastMessageTime = LocalDateTime.now().minusMinutes(5).atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                lastMessage = "Hey, how are you doing today?",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Jane Smith",
                lastMessageTime = LocalDateTime.now().minusHours(2).atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                lastMessage = "Can we meet tomorrow for the project discussion?",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Mike Johnson",
                lastMessageTime = LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                lastMessage = "Thanks for the help with the code review!",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Sarah Wilson",
                lastMessageTime = LocalDateTime.now().minusWeeks(1).atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                lastMessage = "This is a very long message to test how the trimming functionality works in the conversation list",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "David Brown",
                lastMessageTime = LocalDateTime.now().minusMonths(1).atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                lastMessage = "See you later!",
                profileImageRes = R.drawable.avatar_image_placeholder
            )
        )
        allConversations = sampleConversations
        filteredConversations = sampleConversations
        adapter.submitList(sampleConversations)
    }
}