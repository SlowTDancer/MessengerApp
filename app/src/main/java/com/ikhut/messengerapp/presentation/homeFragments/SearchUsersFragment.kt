package com.ikhut.messengerapp.presentation.homeFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ikhut.messengerapp.R
import com.ikhut.messengerapp.databinding.FragmentSearchUsersBinding
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.presentation.activity.BottomAppBarController
import com.ikhut.messengerapp.presentation.adapters.SearchUsersAdapter
import com.ikhut.messengerapp.presentation.components.VerticalSpaceItemDecoration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchUsersFragment : Fragment() {

    private var _binding: FragmentSearchUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchUsersAdapter: SearchUsersAdapter
    private var allUsers: List<User> = emptyList()
    private var filteredUsers: List<User> = emptyList()
    private var searchJob: Job? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchUsersBinding.inflate(inflater, container, false)

        initRecyclerView()
        initListeners()
        setupSearchView()
        loadSampleData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottopAppBarController = (activity as? BottomAppBarController)
        bottopAppBarController?.hideBottomAppBar()
        bottopAppBarController?.hideFab()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
        _binding = null

        val bottopAppBarController = (activity as? BottomAppBarController)
        bottopAppBarController?.showBottomAppBar()
        bottopAppBarController?.showFab()
    }

    private fun initRecyclerView() {
        searchUsersAdapter = SearchUsersAdapter()
        binding.recyclerView.apply {
            adapter = searchUsersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val spacingInPixels =
            resources.getDimensionPixelSize(R.dimen.user_profile_summaries_spacing)
        binding.recyclerView.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))
    }

    fun initListeners() {
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText ?: ""

                searchJob?.cancel()

                if (query.length < 3) {
                    searchUsersAdapter.submitList(allUsers)
                } else {
                    searchJob = lifecycleScope.launch {
                        delay(300)
                        filterUsers(query)
                    }
                }
                return true
            }
        })
    }

    private fun filterUsers(query: String) {
        filteredUsers = if (query.isEmpty()) {
            allUsers
        } else {
            allUsers.filter { user ->
                user.username.contains(query, ignoreCase = true) ||
                        user.job.contains(query, ignoreCase = true)
            }
        }
        searchUsersAdapter.submitList(filteredUsers)
    }

    private fun loadSampleData() {
        val sampleUsers = listOf(
            User(
                username = "john_doe",
                job = "Software Engineer"
            ),
            User(
                username = "jane_smith",
                job = "UI/UX Designer"
            ),
            User(
                username = "mike_johnson",
                job = "Product Manager"
            ),
            User(
                username = "sarah_wilson",
                job = "Data Scientist"
            ),
            User(
                username = "alex_brown",
                job = "DevOps Engineer"
            ),
            User(
                username = "emily_davis",
                job = "Marketing Specialist"
            ),
            User(
                username = "chris_taylor",
                job = "Business Analyst"
            ),
            User(
                username = "lisa_anderson",
                job = "Graphic Designer"
            ),
            User(
                username = "david_martinez",
                job = "Full Stack Developer"
            ),
            User(
                username = "rachel_garcia",
                job = "Project Manager"
            )
        )

        allUsers = sampleUsers
        filteredUsers = sampleUsers
        searchUsersAdapter.submitList(sampleUsers)
    }
}