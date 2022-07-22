package com.blackeyedghoul.cochat.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blackeyedghoul.cochat.R
import com.blackeyedghoul.cochat.models.Message
import com.blackeyedghoul.cochat.models.User
import java.text.SimpleDateFormat

class ChatAdapter(
    private val messagesList: ArrayList<Message>,
    private val sender: User,
    private val isDefaultBackground: Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val SENT = 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (messagesList[position].senderUid == sender.uid) 0
        else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SENT) {
           ViewHolderSent(LayoutInflater.from(parent.context).inflate(R.layout.chat_sent_message_box, parent, false))
        } else {
            ViewHolderReceived(LayoutInflater.from(parent.context).inflate(R.layout.chat_received_message_box, parent, false))
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messagesList[position]
        val date = SimpleDateFormat("HH:mm").format(msg.timestamp.toDate())

        if (holder is ViewHolderSent) {
            holder.msg.text = msg.message
            holder.date.text = date

            if (!isDefaultBackground) {
                holder.date.setTextColor(Color.WHITE)
            }

        } else if (holder is ViewHolderReceived) {
            holder.msg.text = msg.message
            holder.date.text = date

            if (!isDefaultBackground) {
                holder.date.setTextColor(Color.WHITE)
            }
        }
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    class ViewHolderSent(view: View) : RecyclerView.ViewHolder(view) {
        val msg: TextView = view.findViewById(R.id.ch_sent_message)
        val date: TextView = view.findViewById(R.id.ch_sent_date)
    }

    class ViewHolderReceived(view: View) : RecyclerView.ViewHolder(view) {
        val msg: TextView = view.findViewById(R.id.ch_received_message)
        val date: TextView = view.findViewById(R.id.ch_received_date)
    }
}