package com.ikhut.messengerapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.ikhut.messengerapp.databinding.ActivityHomeBinding
import com.ikhut.messengerapp.presentation.authentification.LoginFragment
import com.ikhut.messengerapp.presentation.homeFragments.ConversationListFragment

class HomeActivity : AppCompatActivity() {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.background = null

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.homeActivityFragmentContainerView.id, ConversationListFragment()).commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}