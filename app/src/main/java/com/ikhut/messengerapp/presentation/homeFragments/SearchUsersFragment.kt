package com.ikhut.messengerapp.presentation.homeFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ikhut.messengerapp.databinding.FragmentSearchUsersBinding
import com.ikhut.messengerapp.presentation.activity.BottomAppBarController

class SearchUsersFragment : Fragment() {

    private var _binding: FragmentSearchUsersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchUsersBinding.inflate(inflater, container, false)
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
        _binding = null

        val bottopAppBarController = (activity as? BottomAppBarController)
        bottopAppBarController?.showBottomAppBar()
        bottopAppBarController?.showFab()
    }


}