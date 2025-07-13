package com.ikhut.messengerapp.presentation.homeFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.ikhut.messengerapp.R
import com.ikhut.messengerapp.databinding.FragmentConversationListBinding
import com.ikhut.messengerapp.domain.model.ConversationSummary
import java.time.LocalDateTime
import kotlin.math.abs

class ConversationListFragment : Fragment() {

    private var _binding: FragmentConversationListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ConversationSummaryAdapter

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

    private fun loadSampleData() {
        val sampleConversations = listOf(
            ConversationSummary(
                addresseeName = "Sayed Eftiaz",
                lastMessageTime = LocalDateTime.now().minusMinutes(5),
                lastMessage = "On my way home but I needed to stop by the block store to get some items",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "John Doe",
                lastMessageTime = LocalDateTime.now().minusMinutes(5),
                lastMessage = "Hey, how are you doing today?",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Jane Smith",
                lastMessageTime = LocalDateTime.now().minusHours(2),
                lastMessage = "Can we meet tomorrow for the project discussion?",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Mike Johnson",
                lastMessageTime = LocalDateTime.now().minusDays(1),
                lastMessage = "Thanks for the help with the code review!",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Sarah Wilson",
                lastMessageTime = LocalDateTime.now().minusWeeks(1),
                lastMessage = "This is a very long message to test how the trimming functionality works in the conversation list",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "David Brown",
                lastMessageTime = LocalDateTime.now().minusMonths(1),
                lastMessage = "See you later!",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Sayed Eftiaz",
                lastMessageTime = LocalDateTime.now().minusMinutes(5),
                lastMessage = "On my way home but I needed to stop by the block store to...",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "John Doe",
                lastMessageTime = LocalDateTime.now().minusMinutes(5),
                lastMessage = "Hey, how are you doing today?",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Jane Smith",
                lastMessageTime = LocalDateTime.now().minusHours(2),
                lastMessage = "Can we meet tomorrow for the project discussion?",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Mike Johnson",
                lastMessageTime = LocalDateTime.now().minusDays(1),
                lastMessage = "Thanks for the help with the code review!",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "Sarah Wilson",
                lastMessageTime = LocalDateTime.now().minusWeeks(1),
                lastMessage = "This is a very long message to test how the trimming functionality works in the conversation list",
                profileImageRes = R.drawable.avatar_image_placeholder
            ), ConversationSummary(
                addresseeName = "David Brown",
                lastMessageTime = LocalDateTime.now().minusMonths(1),
                lastMessage = "See you later!",
                profileImageRes = R.drawable.avatar_image_placeholder
            )
        )
        adapter.submitList(sampleConversations)
    }
}

interface BottomAppBarController {
    fun hideBottomAppBar()
    fun showBottomAppBar()
}
