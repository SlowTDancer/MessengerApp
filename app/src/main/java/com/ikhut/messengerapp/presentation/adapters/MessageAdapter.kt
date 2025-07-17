package com.ikhut.messengerapp.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ikhut.messengerapp.databinding.MessageReceivedLayoutBinding
import com.ikhut.messengerapp.databinding.MessageSentLayoutBinding
import com.ikhut.messengerapp.domain.model.Message
import com.ikhut.messengerapp.domain.model.User
import com.ikhut.messengerapp.presentation.viewHolders.ReceivedMessageViewHolder
import com.ikhut.messengerapp.presentation.viewHolders.SentMessageViewHolder

class MessageAdapter(
    private val currentUser: User
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    private var messages: List<Message> = emptyList()

    fun submitList(newMessages: List<Message>, commitCallback: Runnable?) {
//        Log.d("MessageAdapter", "submitList with callback called with ${newMessages.size} messages")
        val diffCallback = MessageDiffCallback(messages, newMessages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        messages = newMessages
        diffResult.dispatchUpdatesTo(this)
        commitCallback?.run()
//        Log.d("MessageAdapter", "DiffUtil updates dispatched with callback, new size: ${messages.size}")
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        val viewType = if (message.senderId == currentUser.username) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
//        Log.d("MessageAdapter", "Position $position: senderId=${message.senderId}, currentUser=${currentUser.username}, viewType=$viewType")
        return viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val binding = MessageSentLayoutBinding.inflate(inflater, parent, false)
                SentMessageViewHolder(binding)
            }

            VIEW_TYPE_RECEIVED -> {
                val binding = MessageReceivedLayoutBinding.inflate(inflater, parent, false)
                ReceivedMessageViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
//        Log.d("MessageAdapter", "Binding message at position $position: ${message.content}")
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int {
//        Log.d("MessageAdapter", "getItemCount: ${messages.size}")
        return messages.size
    }
}

class MessageDiffCallback(
    private val oldList: List<Message>, private val newList: List<Message>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val same = oldList[oldItemPosition].id == newList[newItemPosition].id
//        Log.d("MessageDiffCallback", "areItemsTheSame: ${oldList[oldItemPosition].id} == ${newList[newItemPosition].id} = $same")
        return same
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val same = oldList[oldItemPosition] == newList[newItemPosition]
//        Log.d("MessageDiffCallback", "areContentsTheSame: $same")
        return same
    }
}