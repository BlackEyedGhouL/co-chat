package com.blackeyedghoul.cochat

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blackeyedghoul.cochat.models.User
import com.google.android.material.textfield.TextInputEditText

class Chats : AppCompatActivity() {

    private var alertDialog: AlertDialog? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var back: ImageView
    private lateinit var name: TextView
    private lateinit var status: TextView
    private lateinit var message: TextInputEditText
    private lateinit var send: CardView
    private lateinit var user: User
    private lateinit var profilePicture: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        init()

        back.setOnClickListener{
            onBackPressed()
        }

        (recyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true
        user = intent.getParcelableExtra("USER")!!
        name.text = user.username
        setProfilePicture()

        checkNetworkConnection()
    }

    private fun setProfilePicture() {
        when (user.profilePicture) {
            "01" -> {
                profilePicture.setImageResource(R.drawable.pp_1)
            }
            "02" -> {
                profilePicture.setImageResource(R.drawable.pp_2)
            }
            "03" -> {
                profilePicture.setImageResource(R.drawable.pp_3)
            }
            "04" -> {
                profilePicture.setImageResource(R.drawable.pp_4)
            }
            "05" -> {
                profilePicture.setImageResource(R.drawable.pp_5)
            }
            "06" -> {
                profilePicture.setImageResource(R.drawable.pp_6)
            }
            "07" -> {
                profilePicture.setImageResource(R.drawable.pp_7)
            }
            "08" -> {
                profilePicture.setImageResource(R.drawable.pp_8)
            }
            "09" -> {
                profilePicture.setImageResource(R.drawable.pp_9)
            }
            "10" -> {
                profilePicture.setImageResource(R.drawable.pp_10)
            }
            "11" -> {
                profilePicture.setImageResource(R.drawable.pp_11)
            }
            "12" -> {
                profilePicture.setImageResource(R.drawable.pp_12)
            }
            "13" -> {
                profilePicture.setImageResource(R.drawable.pp_13)
            }
            "14" -> {
                profilePicture.setImageResource(R.drawable.pp_14)
            }
            "15" -> {
                profilePicture.setImageResource(R.drawable.pp_15)
            }
            "16" -> {
                profilePicture.setImageResource(R.drawable.pp_16)
            }
        }
    }

    private fun init() {
        recyclerView = findViewById(R.id.ch_chat)
        back = findViewById(R.id.ch_back)
        name = findViewById(R.id.ch_username)
        status = findViewById(R.id.ch_status)
        message = findViewById(R.id.ch_message_txt)
        send = findViewById(R.id.ch_send_card)
        profilePicture = findViewById(R.id.ch_profile_picture)
    }

    private fun checkNetworkConnection() {
        val networkConnection = InternetConnection(this)
        networkConnection.observe(this) { isConnected ->

            val view = View.inflate(this, R.layout.no_internet_alert, null)
            val builder = AlertDialog.Builder(this, R.style.FullscreenAlertDialog)
            builder.setView(view)

            if (isConnected) {
                Log.d(ContentValues.TAG, "NetworkConnection: true")
                alertDialog?.dismiss()
            } else {
                Log.d(ContentValues.TAG, "NetworkConnection: false")
                alertDialog = builder.create()
                alertDialog!!.window?.setBackgroundDrawableResource(android.R.color.white)
                alertDialog!!.show()

                val dismiss = alertDialog!!.findViewById(R.id.ni_dismiss) as? Button
                dismiss?.setOnClickListener {
                    alertDialog?.dismiss()
                }
            }
        }
    }
}