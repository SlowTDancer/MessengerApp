package com.ikhut.messengerapp.presentation.homeFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.ikhut.messengerapp.R
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.application.getConversationSummaryRepository
import com.ikhut.messengerapp.application.getUserSessionManager
import com.ikhut.messengerapp.databinding.FragmentConversationListBinding
import com.ikhut.messengerapp.domain.common.Resource
import com.ikhut.messengerapp.domain.model.ConversationSummary
import com.ikhut.messengerapp.presentation.activity.BottomAppBarController
import com.ikhut.messengerapp.presentation.activity.ChatActivity
import com.ikhut.messengerapp.presentation.adapters.ConversationSummaryAdapter
import com.ikhut.messengerapp.presentation.components.VerticalSpaceItemDecoration
import com.ikhut.messengerapp.presentation.viewmodel.ConversationSummaryViewModel
import kotlin.math.abs

class ConversationListFragment : Fragment() {

    private var _binding: FragmentConversationListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ConversationSummaryAdapter
    private lateinit var conversationSummaryViewModel: ConversationSummaryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConversationListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conversationSummaryViewModel = ViewModelProvider(
            this, ConversationSummaryViewModel.create(
                getConversationSummaryRepository(),
                getUserSessionManager().currentUser?.username ?: ""
            )
        )[ConversationSummaryViewModel::class.java]

        setCollapsingViewAnimation()
        setupRecyclerView()
        setupSearchView()
        setupPagination()
        observeViewModel()
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

            val alpha = 1.0f - percentage

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
        adapter = ConversationSummaryAdapter { conversation ->
            onConversationClick(conversation)
        }
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

    private fun setupPagination() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItemPosition >= totalItemCount - 5) {
                    conversationSummaryViewModel.loadMoreConversations()
                }
            }
        })
    }

    private fun observeViewModel() {
        conversationSummaryViewModel.conversations.observe(viewLifecycleOwner) { conversations ->
            val currentQuery = binding.searchView.query.toString()
            filterConversations(currentQuery, conversations)

            val resource = conversationSummaryViewModel.conversationsState.value
            val isLoading = resource is Resource.Loading
            val hasError = resource is Resource.Error

            val filteredConversations = if (currentQuery.isEmpty()) {
                conversations
            } else {
                conversations.filter { conversation ->
                    conversation.addresseeName.contains(currentQuery, ignoreCase = true)
                }
            }

            updateCenteredTextVisibility(filteredConversations, isLoading, hasError)
        }

        conversationSummaryViewModel.conversationsState.observe(viewLifecycleOwner) { resource ->
            val isLoading = resource is Resource.Loading
            val hasError = resource is Resource.Error
            val errorMessage = if (resource is Resource.Error) resource.message else null

            val conversations = conversationSummaryViewModel.conversations.value ?: emptyList()
            val currentQuery = binding.searchView.query.toString()

            val filteredConversations = if (currentQuery.isEmpty()) {
                conversations
            } else {
                conversations.filter { conversation ->
                    conversation.addresseeName.contains(currentQuery, ignoreCase = true)
                }
            }

            updateCenteredTextVisibility(filteredConversations, isLoading, hasError, errorMessage)
        }
    }

    private fun filterConversations(
        query: String, conversations: List<ConversationSummary>? = null
    ) {
        val conversationsToFilter =
            conversations ?: conversationSummaryViewModel.conversations.value ?: emptyList()

        val filteredConversations = if (query.isEmpty()) {
            conversationsToFilter
        } else {
            conversationsToFilter.filter { conversation ->
                conversation.addresseeName.contains(query, ignoreCase = true)
            }
        }

        adapter.submitList(filteredConversations)

        val resource = conversationSummaryViewModel.conversationsState.value
        val isLoading = resource is Resource.Loading
        val hasError = resource is Resource.Error
        val errorMessage = if (resource is Resource.Error) resource.message else null


        updateCenteredTextVisibility(filteredConversations, isLoading, hasError, errorMessage)
    }

    private fun updateCenteredTextVisibility(
        conversations: List<ConversationSummary>,
        isLoading: Boolean,
        hasError: Boolean,
        errorMessage: String? = null
    ) {
        when {
            hasError -> {
                binding.centeredText.text =
                    errorMessage ?: getString(R.string.error_loading_conversations)
                binding.centeredText.visibility = View.VISIBLE
            }

            !isLoading && conversations.isEmpty() -> {
                binding.centeredText.text = getString(R.string.no_conversations)
                binding.centeredText.visibility = View.VISIBLE
            }

            else -> {
                binding.centeredText.visibility = View.GONE
            }
        }
    }

    private fun onConversationClick(conversation: ConversationSummary) {
        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra(Constants.EXTRA_USERNAME, conversation.addresseeName)
        }
        startActivity(intent)
    }
}