package com.ikhut.messengerapp.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ikhut.messengerapp.databinding.UserProfileSummaryLayoutBinding
import com.ikhut.messengerapp.domain.model.ConversationSummary
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.presentation.viewHolders.SearchUsersViewHolder

class SearchUsersAdapter(
    private val onItemClick: (User) -> Unit
): RecyclerView.Adapter<SearchUsersViewHolder>() {
    private var users: List<User> = emptyList()

    fun submitList(newUsers: List<User>) {
        val diffCallback = UserDiffCallback(users, newUsers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        users = newUsers
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SearchUsersViewHolder {
        val binding = UserProfileSummaryLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SearchUsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchUsersViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)

        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }

    override fun getItemCount(): Int = users.size
}

class UserDiffCallback(
    private val oldList: List<User>, private val newList: List<User>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].username == newList[newItemPosition].username
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}