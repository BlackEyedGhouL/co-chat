package com.blackeyedghoul.cochat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blackeyedghoul.cochat.R
import com.blackeyedghoul.cochat.models.Contact
import com.blackeyedghoul.cochat.models.User

class InviteContactsAdapter(private val inviteUsersList: ArrayList<Contact>, private val context: Context): RecyclerView.Adapter<InviteContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.invite_user_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Contact = inviteUsersList[position]
        holder.fullName.text = user.name

        holder.invite.setOnClickListener{

        }
    }

    override fun getItemCount(): Int {
        return inviteUsersList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val fullName: TextView = itemView.findViewById(R.id.c_invite_user_card_full_name)
        val invite: Button = itemView.findViewById(R.id.c_invite_user_card_button)
    }
}