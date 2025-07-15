package com.ikhut.messengerapp.presentation.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.ikhut.messengerapp.databinding.UserProfileSummaryLayoutBinding
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.presentation.utils.ProfilePictureLoader

class SearchUsersViewHolder(private val binding: UserProfileSummaryLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.userName.text = user.username
        binding.userJob.text = user.job

        ProfilePictureLoader.loadProfilePicture(
            context = binding.root.context,
            imageView = binding.profilePicture,
            imageUrl = user.imageURL,
            placeholderName = user.username
        )
    }
}