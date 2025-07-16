package com.ikhut.messengerapp.presentation.homeFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ikhut.messengerapp.R
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.application.getUserRepository
import com.ikhut.messengerapp.application.getUserSessionManager
import com.ikhut.messengerapp.databinding.FragmentSearchUsersBinding
import com.ikhut.messengerapp.presentation.activity.BottomAppBarController
import com.ikhut.messengerapp.presentation.adapters.SearchUsersAdapter
import com.ikhut.messengerapp.presentation.components.VerticalSpaceItemDecoration
import com.ikhut.messengerapp.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchUsersFragment : Fragment() {

    private var _binding: FragmentSearchUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchUsersAdapter: SearchUsersAdapter
    private lateinit var viewModel: UserViewModel

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchUsersBinding.inflate(inflater, container, false)

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        searchUsersAdapter = SearchUsersAdapter()
        viewModel = ViewModelProvider(
            this, UserViewModel.create(
                getUserRepository(), getUserSessionManager().currentUser?.username ?: ""
            )
        )[UserViewModel::class.java]

        initRecyclerView()
        setupSearchView()
        observeViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomAppBarController = (activity as? BottomAppBarController)
        bottomAppBarController?.hideBottomAppBar()
        bottomAppBarController?.hideFab()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
        _binding = null

        val bottomAppBarController = (activity as? BottomAppBarController)
        bottomAppBarController?.showBottomAppBar()
        bottomAppBarController?.showFab()
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            adapter = searchUsersAdapter
            layoutManager = LinearLayoutManager(requireContext())

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 8) {
                        viewModel.loadMore()
                    }
                }
            })
        }

        val spacingInPixels =
            resources.getDimensionPixelSize(R.dimen.user_profile_summaries_spacing)
        binding.recyclerView.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText ?: ""

                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(Constants.SEARCH_DELAY)
                    viewModel.setSearchQuery(query)
                }
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.users.collect { users ->
                searchUsersAdapter.submitList(users)
                updateCenteredTextVisibility(users, viewModel.isLoading.value, hasError = false)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                updateCenteredTextVisibility(viewModel.users.value, isLoading, hasError = false)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorState.collect { error ->
                val hasError = error != null
                updateCenteredTextVisibility(
                    viewModel.users.value, viewModel.isLoading.value, hasError
                )
            }
        }
    }

    private fun updateCenteredTextVisibility(
        users: List<*>, isLoading: Boolean, hasError: Boolean
    ) {
        when {
            hasError -> {
                binding.centeredText.text = getString(R.string.error_loading_users)
                binding.centeredText.visibility = View.VISIBLE
            }

            !isLoading && users.isEmpty() -> {
                binding.centeredText.text = getString(R.string.no_users_to_show)
                binding.centeredText.visibility = View.VISIBLE
            }

            else -> {
                binding.centeredText.visibility = View.GONE
            }
        }
    }
}