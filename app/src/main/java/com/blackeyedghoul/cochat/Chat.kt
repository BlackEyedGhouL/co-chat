package com.blackeyedghoul.cochat

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blackeyedghoul.cochat.models.Message
import com.blackeyedghoul.cochat.models.Room
import com.blackeyedghoul.cochat.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


class Chat : AppCompatActivity() {

    private var alertDialog: AlertDialog? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var back: ImageView
    private lateinit var name: TextView
    private lateinit var status: TextView
    private lateinit var messageBox: TextInputEditText
    private lateinit var send: CardView
    private lateinit var receiver: User
    private lateinit var sender: User
    private lateinit var room: Room
    private lateinit var profilePicture: ImageView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var progressDialogActivity: WelcomeScreen
    private lateinit var chatBackground: ImageView
    private lateinit var configuration: com.blackeyedghoul.cochat.models.Configuration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        init()

        back.setOnClickListener {
            onBackPressed()
        }

        messageBox.addTextChangedListener(messageTextWatcher)

        (recyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true
        receiver = intent.getParcelableExtra("RECEIVER")!!
        sender = intent.getParcelableExtra("SENDER")!!

        checkNetworkConnection()

        progressDialogActivity.showProgressDialog(this)
        name.text = receiver.username
        setProfilePicture()
        setStatus()
        setChatBackground()

        doesRoomExist()
    }

    @SuppressLint("ResourceAsColor")
    private fun setChatBackground() {
        val docRef = db.collection("settings").document(sender.uid)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                configuration = snapshot.toObject<com.blackeyedghoul.cochat.models.Configuration>()!!

                when (configuration.chatBackground) {
                    "01" -> {
                        chatBackground.setImageDrawable(null)
                    }
                    "02" -> {
                        chatBackground.setImageResource(R.drawable.chat_background_2)
                    }
                    "03" -> {
                        chatBackground.setImageResource(R.drawable.chat_background_3)
                    }
                    "04" -> {
                        chatBackground.setImageResource(R.drawable.chat_background_4)
                    }
                    "05" -> {
                        chatBackground.setImageResource(R.drawable.chat_background_5)
                    }
                    "06" -> {
                        chatBackground.setImageResource(R.drawable.chat_background_6)
                    }
                    "07" -> {
                        chatBackground.setImageResource(R.drawable.chat_background_7)
                    }
                    "08" -> {
                        chatBackground.setImageResource(R.drawable.chat_background_8)
                    }
                    "09" -> {
                        chatBackground.setImageResource(R.drawable.chat_background_9)
                    }
                }

                Log.d(TAG, "Current data: ${snapshot.data}")
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setStatus() {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(receiver.uid)

        docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.d(TAG, "Get failed with ", error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val user: User = snapshot.toObject<User>()!!

                if (user.isOnline) {
                    status.text = "Online"
                    receiver.isOnline = true
                } else {
                    status.text = "Offline"
                    receiver.isOnline = false
                }
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }

    private var messageTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val message = messageBox.text.toString().trim()

            if (message.isNotEmpty()) {
                send.setBackgroundResource(R.drawable.btn_bg_proceed_enable)
                send.isClickable = true
                send.isFocusable = true

                send.setOnClickListener {
                    if (room.id != "") {
                        sendMessage(message)
                    } else {
                        Toast.makeText(applicationContext, "Room doesn't exist", Toast.LENGTH_SHORT)
                            .show()
                    }
                    messageBox.text!!.clear()
                }
            } else {
                send.setBackgroundResource(R.drawable.btn_bg_proceed_disable)
                send.isClickable = false
                send.isFocusable = false
            }

            if (!TextUtils.isEmpty(p0)) {
                updateIsTyping(true)
            } else {
                updateIsTyping(false)
            }
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    private fun updateIsTyping(status: Boolean) {
        db.collection("rooms").document(room.id)
            .update(
                "isTyping", listOf(status, room.isTyping[1])
            )
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                return@addOnSuccessListener
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                return@addOnFailureListener
            }
    }

    private fun sendMessage(message: String) {
        val messageRef = db.collection("rooms").document(room.id).collection("messages").document()
        val msg = Message(
            messageRef.id,
            sender.uid,
            receiver.uid,
            Timestamp.now(),
            message
        )
        messageRef.set(msg)
            .addOnSuccessListener {
                updateLastMessage(message)
                Log.d(TAG, "DocumentSnapshot successfully created!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error creating document", e)
                Toast.makeText(
                    applicationContext,
                    "Message send failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                return@addOnFailureListener
            }
    }

    private fun updateLastMessage(message: String) {
        db.collection("rooms").document(room.id)
            .update(
                "lastMessage", message
            )
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                updateLastModifiedTime()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                return@addOnFailureListener
            }
    }

    private fun updateLastModifiedTime() {
        db.collection("rooms").document(room.id)
            .update(
                "lastUpdatedTimestamp", Timestamp.now()
            )
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                return@addOnSuccessListener
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                return@addOnFailureListener
            }
    }

    private fun doesRoomExist() {
        if (sender.rooms.isNullOrEmpty()) {
            createRoom()
        } else {
            sender.rooms!!.forEach { roomId ->
                val docRef = db.collection("rooms").document(roomId)
                docRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        Log.d(TAG, "DocumentSnapshot data: ${documentSnapshot.data}")
                        val tempRoom = documentSnapshot.toObject<Room>()
                        val tempSender = tempRoom!!.members[0]
                        val tempReceiver = tempRoom.members[1]

                        if (tempReceiver == receiver.uid) {
                            listenForRoomChanges(tempRoom.id)
                            Log.d(TAG, "Room exists: true, Id: ${tempRoom.id}")
                            return@addOnSuccessListener
                        } else if (tempSender == receiver.uid) {
                            listenForRoomChanges(tempRoom.id)
                            Log.d(TAG, "Room exists: true, Id: ${tempRoom.id}")
                            return@addOnSuccessListener
                        } else {
                            Log.d(TAG, "Room exists: false")
                            createRoom()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Get failed with ", exception)
                        Toast.makeText(applicationContext, exception.message, Toast.LENGTH_SHORT)
                            .show()
                        return@addOnFailureListener
                    }
            }
        }
        progressDialogActivity.dismissProgressDialog()
    }

    @SuppressLint("SetTextI18n")
    private fun listenForRoomChanges(id: String) {
        val docRef = db.collection("rooms").document(id)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                room = snapshot.toObject<Room>()!!

                if (room.isTyping[1]) status.text = "Typing"

                Log.d(TAG, "Current data: ${snapshot.data}")
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }

    private fun createRoom() {
        val roomRef = db.collection("rooms").document()
        room = Room(
            roomRef.id,
            listOf(sender.uid, receiver.uid),
            listOf(false, false),
            null,
            ""
        )
        roomRef.set(room)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully created!")
                addRoomToUsers(roomRef.id, receiver)
                addRoomToUsers(roomRef.id, sender)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error creating document", e)
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }

    private fun addRoomToUsers(id: String, user: User) {
        val oldRoomList = user.rooms
        val newRoomList: List<String> = if (oldRoomList.isNullOrEmpty()) {
            listOf(id)
        } else {
            oldRoomList.plus(id)
        }

        db.collection("users").document(user.uid)
            .update(
                "rooms", newRoomList
            )
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                return@addOnSuccessListener
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }

    private fun setProfilePicture() {
        when (receiver.profilePicture) {
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
        messageBox = findViewById(R.id.ch_message_txt)
        send = findViewById(R.id.ch_send_card)
        progressDialogActivity = WelcomeScreen()
        profilePicture = findViewById(R.id.ch_profile_picture)
        chatBackground = findViewById(R.id.ch_background)
    }

    private fun checkNetworkConnection() {
        val networkConnection = InternetConnection(this)
        networkConnection.observe(this) { isConnected ->

            val view = View.inflate(this, R.layout.no_internet_alert, null)
            val builder = AlertDialog.Builder(this, R.style.FullscreenAlertDialog)
            builder.setView(view)

            if (isConnected) {
                Log.d(TAG, "NetworkConnection: true")
                alertDialog?.dismiss()
            } else {
                Log.d(TAG, "NetworkConnection: false")
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