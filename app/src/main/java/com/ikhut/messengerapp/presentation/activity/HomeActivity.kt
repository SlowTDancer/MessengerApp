package com.ikhut.messengerapp.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ikhut.messengerapp.R
import com.ikhut.messengerapp.databinding.ActivityHomeBinding
import com.ikhut.messengerapp.presentation.homeFragments.ConversationListFragment
import com.ikhut.messengerapp.presentation.homeFragments.SearchUsersFragment
import com.ikhut.messengerapp.presentation.homeFragments.SettingsFragment

class HomeActivity : AppCompatActivity(), BottomAppBarController {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigationView()
    }

    private fun initBottomNavigationView() {
        binding.bottomNavigationView.background = null

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction().replace(
                        binding.homeActivityFragmentContainerView.id, ConversationListFragment()
                    ).commit()
                    true
                }

                R.id.nav_settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.homeActivityFragmentContainerView.id, SettingsFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }

        binding.fab.setOnClickListener { item ->
            supportFragmentManager.beginTransaction().replace(
                binding.homeActivityFragmentContainerView.id, SearchUsersFragment()
            ).commit()
            true
        }

        binding.bottomNavigationView.selectedItemId = R.id.nav_home
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun hideBottomAppBar() {
        binding.bottomAppBar.performHide()
    }

    override fun showBottomAppBar() {
        binding.bottomAppBar.performShow()
    }

    override fun hideFab() {
        binding.fab.hide()
    }

    override fun showFab() {
        binding.fab.show()
    }
}

interface BottomAppBarController {
    fun hideBottomAppBar()
    fun showBottomAppBar()
    fun hideFab()
    fun showFab()
}