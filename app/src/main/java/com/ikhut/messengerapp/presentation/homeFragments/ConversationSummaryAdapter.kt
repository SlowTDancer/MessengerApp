package com.ikhut.messengerapp.presentation.homeFragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ikhut.messengerapp.databinding.ConversationSummaryLayoutBinding
import com.ikhut.messengerapp.domain.model.ConversationSummary

class ConversationSummaryAdapter : RecyclerView.Adapter<ConversationSummaryViewHolder>() {
    private var conversations: List<ConversationSummary> = emptyList()

    fun submitList(newConversations: List<ConversationSummary>) {
        val diffCallback = ConversationDiffCallback(conversations, newConversations)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        conversations = newConversations
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ConversationSummaryViewHolder {
        val binding = ConversationSummaryLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ConversationSummaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationSummaryViewHolder, position: Int) {
        val conversation = conversations[position]
        holder.bind(conversation)
    }

    override fun getItemCount(): Int = conversations.size
}

class ConversationDiffCallback(
    private val oldList: List<ConversationSummary>, private val newList: List<ConversationSummary>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].addresseeName == newList[newItemPosition].addresseeName
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}