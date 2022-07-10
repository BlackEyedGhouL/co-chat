package com.blackeyedghoul.cochat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blackeyedghoul.cochat.R
import com.blackeyedghoul.cochat.models.User

class ContactsAdapter(private val usersList: ArrayList<User>): RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contacts_user_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = usersList[position]
        holder.fullName.text = user.username
        holder.bio.text = user.bio

        when (user.profilePicture) {
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
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val fullName: TextView = itemView.findViewById(R.id.c_user_card_full_name)
        val bio: TextView = itemView.findViewById(R.id.c_user_card_bio)
        val profilePicture: ImageView = itemView.findViewById(R.id.c_user_card_profile_picture)
    }
}
