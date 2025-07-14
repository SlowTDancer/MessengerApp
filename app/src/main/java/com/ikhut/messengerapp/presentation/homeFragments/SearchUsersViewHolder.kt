package com.ikhut.messengerapp.presentation.homeFragments

import androidx.recyclerview.widget.RecyclerView
import com.ikhut.messengerapp.R
import com.ikhut.messengerapp.databinding.UserProfileSummaryLayoutBinding
import com.ikhut.messengerapp.domain.model.User

class SearchUsersViewHolder(private val binding: UserProfileSummaryLayoutBinding) :
    RecyclerView.ViewHolder(binding.root)  {

    fun bind(user: User) {
        binding.userName.text = user.username
        binding.userJob.text = user.job

        //TODO: this may need to be changed
        binding.profilePicture.setImageResource(
            R.drawable.avatar_image_placeholder
        )
    }
}