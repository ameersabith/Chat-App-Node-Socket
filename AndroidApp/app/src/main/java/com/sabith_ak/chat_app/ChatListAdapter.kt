package com.sabith_ak.chat_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sabith_ak.chat_app.model.Message

class ChatListAdapter(
        private val context: Context,
        private val inflater: LayoutInflater
) : RecyclerView.Adapter<ChatListAdapter.ViewFinder>() {

    private val messages: MutableList<Message> = mutableListOf()

    open class ViewFinder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView? = null
        var tvMessageContent: TextView? = null
        var tvUserInfo: TextView? = null

        init {
            when (viewType) {
                CHAT_MINE -> {
                    tvMessageContent = itemView.findViewById(R.id.tv_message_send)
                }

                CHAT_PARTNER -> {
                    tvName = itemView.findViewById(R.id.tv_username)
                    tvMessageContent = itemView.findViewById(R.id.tv_message_received)
                }

                USER_JOIN -> {
                    tvUserInfo = itemView.findViewById(R.id.tv_user_info)
                }

                USER_LEAVE -> {
                    tvUserInfo = itemView.findViewById(R.id.tv_user_info)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewFinder {
        val view: View? = null
        when (viewType) {
            CHAT_MINE -> {
                return ViewFinder(
                        inflater.inflate(R.layout.item_sent_message, parent, false),
                        viewType
                )
            }

            CHAT_PARTNER -> {
                return ViewFinder(
                        inflater.inflate(R.layout.item_received_message, parent, false),
                        viewType
                )
            }

            USER_JOIN -> {
                return ViewFinder(
                        inflater.inflate(R.layout.chat_into_notification, parent, false),
                        viewType
                )
            }

            USER_LEAVE -> {
                return ViewFinder(
                        inflater.inflate(R.layout.chat_into_notification, parent, false),
                        viewType
                )
            }
        }
        return ViewFinder(view!!, viewType)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return messages[position].viewType
    }

    override fun onBindViewHolder(holder: ViewFinder, position: Int) {
        val messageData  = messages[position]
        val userName = messageData.name
        val content = messageData.messageContent
        when(messageData.viewType) {
            CHAT_MINE -> {
                holder.tvMessageContent!!.text = content
            }
            CHAT_PARTNER ->{
                holder.tvName!!.text = userName
                holder.tvMessageContent!!.text = content
            }
            USER_JOIN -> {
                val text = "$userName has entered the room"
                holder.tvUserInfo!!.text = text
            }
            USER_LEAVE -> {
                val text = "$userName has leaved the room"
                holder.tvUserInfo!!.text = text
            }
        }
    }

    fun addItem(message: Message) {
        messages.add(message)
        notifyDataSetChanged()
    }

    companion object {
        const val CHAT_MINE = 0
        const val CHAT_PARTNER = 1
        const val USER_JOIN = 2
        const val USER_LEAVE = 3
    }
}