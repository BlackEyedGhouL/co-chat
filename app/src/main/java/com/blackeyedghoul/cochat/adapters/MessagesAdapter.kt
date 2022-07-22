package com.blackeyedghoul.cochat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.blackeyedghoul.cochat.Chat
import com.blackeyedghoul.cochat.R
import com.blackeyedghoul.cochat.models.Conversation
import com.blackeyedghoul.cochat.models.Room
import com.blackeyedghoul.cochat.models.User
import java.text.SimpleDateFormat

class MessagesAdapter(
    private val conversationsList: ArrayList<Conversation>,
    private val context: Context
): RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_user_card, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message: Room = conversationsList[position].room
        val receiver: User = conversationsList[position].receiver
        val sender: User = conversationsList[position].sender
        holder.fullName.text = receiver.username
        holder.message.text = message.lastMessage

        val todayFormatted = SimpleDateFormat("dd/MM/yyyy").format(message.lastUpdatedTimestamp!!.toDate())
        val dateFormatted = SimpleDateFormat("dd/MM/yyyy").format(message.lastUpdatedTimestamp!!.toDate())

        if (todayFormatted == dateFormatted) {
            holder.time.text = SimpleDateFormat("HH:mm").format(message.lastUpdatedTimestamp!!.toDate())
        } else {
            holder.time.text = SimpleDateFormat("MMM d").format(message.lastUpdatedTimestamp!!.toDate())
        }

        if (receiver.isOnline)
            holder.status.visibility = View.VISIBLE
        else
            holder.status.visibility = View.INVISIBLE

        when (receiver.profilePicture) {
            "01" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_1)
            }
            "02" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_2)
            }
            "03" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_3)
            }
            "04" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_4)
            }
            "05" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_5)
            }
            "06" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_6)
            }
            "07" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_7)
            }
            "08" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_8)
            }
            "09" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_9)
            }
            "10" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_10)
            }
            "11" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_11)
            }
            "12" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_12)
            }
            "13" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_13)
            }
            "14" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_14)
            }
            "15" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_15)
            }
            "16" -> {
                holder.profilePicture.setImageResource(R.drawable.pp_16)
            }
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(context, Chat::class.java)
            intent.putExtra("RECEIVER", receiver)
            intent.putExtra("SENDER", sender)
            intent.putExtra("ROOM_ID", message.id)
            intent.putExtra("ROOM", message)
            context.startActivity(intent)
        }

        holder.profilePicture.setOnClickListener{}
    }

    override fun getItemCount(): Int {
        return conversationsList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val fullName: TextView = itemView.findViewById(R.id.h_user_card_full_name)
        val time: TextView = itemView.findViewById(R.id.h_user_card_time)
        val profilePicture: ImageView = itemView.findViewById(R.id.h_user_card_profile_picture)
        val message: TextView = itemView.findViewById(R.id.h_user_card_last_message)
        val status: CardView = itemView.findViewById(R.id.h_user_card_profile_picture_online_icon)
    }
}