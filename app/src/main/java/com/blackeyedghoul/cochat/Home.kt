package com.blackeyedghoul.cochat

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.RecyclerView
import com.blackeyedghoul.cochat.adapters.MessagesAdapter
import com.blackeyedghoul.cochat.models.Configuration
import com.blackeyedghoul.cochat.models.Conversation
import com.blackeyedghoul.cochat.models.Room
import com.blackeyedghoul.cochat.models.User
import com.blackeyedghoul.cochat.services.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*


class Home : CheckAvailability(), LifecycleObserver {

    private lateinit var auth: FirebaseAuth
    private lateinit var sender: User
    private lateinit var contacts: ImageView
    private lateinit var profile: ImageView
    private lateinit var search: SearchView
    private lateinit var greeting: TextView
    private lateinit var progressDialogActivity: WelcomeScreen
    private val contactPermissionCode = 1
    private lateinit var contactsActivity: Contacts
    private var alertDialog: AlertDialog? = null
    val db = FirebaseFirestore.getInstance()
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var conversationsArrayList: ArrayList<Conversation>
    private lateinit var backupConversationsArrayList: ArrayList<Conversation>
    private lateinit var searchConversationsArrayList: ArrayList<Conversation>
    private lateinit var noResults: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        init()

        progressDialogActivity.showProgressDialog(this)
        checkNetworkConnection()

        messagesAdapter = MessagesAdapter(searchConversationsArrayList, this@Home)
        messagesRecyclerView.adapter = messagesAdapter

        fetchConfigurations(object: FetchConfigurationCallback{
            override fun onCallback(configs: Configuration) {

                if (configs.isPushNotificationEnabled) {
                    FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
                    FirebaseService.token = FirebaseMessaging.getInstance().token.toString()
                    FirebaseMessaging.getInstance().subscribeToTopic("/topics/${auth.currentUser!!.uid}")
                }
            }
        })

        contacts.setOnClickListener {
            if (checkContactsPermission()) {
                val intent = Intent(this, Contacts::class.java)
                intent.putExtra("SENDER", sender)
                startActivity(intent)
            } else {
                requestContactsPermission()
            }
        }

        profile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        search.setOnClickListener {
            search.isIconified = false
        }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText!!.isNotEmpty()) {
                    searchConversationsArrayList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    backupConversationsArrayList.forEach {
                        if (it.receiver.username.lowercase(Locale.getDefault()).contains(search)) {
                            searchConversationsArrayList.add(it)
                        }
                    }

                    messagesRecyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    searchConversationsArrayList.clear()
                    searchConversationsArrayList.addAll(backupConversationsArrayList)
                    messagesRecyclerView.adapter!!.notifyDataSetChanged()
                }

                if (messagesAdapter.itemCount == 0) {

                    if (newText.isNotEmpty())
                        noResults.text = "No results found for '$newText'"
                    else
                        noResults.text = "No messages found"

                    noResults.visibility = View.VISIBLE
                } else {
                    noResults.visibility = View.GONE
                }

                return true
            }
        })
    }

    private fun fetchConfigurations(fetchConfigurationCallback: FetchConfigurationCallback) {
        val docRef = db.collection("settings").document(auth.currentUser!!.uid)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            val configuration: Configuration

            if (snapshot != null && snapshot.exists()) {
                configuration = snapshot.toObject<Configuration>()!!
                fetchConfigurationCallback.onCallback(configuration)

            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }

    interface FetchConfigurationCallback {
        fun onCallback(configs: Configuration)
    }

    override fun onStart() {
        super.onStart()
        searchConversationsArrayList.clear()
        conversationsArrayList.clear()
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

                fetchSender(object: FetchSenderCallback {
                    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
                    override fun onCallback(user: User) {
                        val firstName = getFirstWord(sender.username).trim()
                        greeting.text = "Hello $firstName,"
                        setProfilePicture(sender.profilePicture)
                    }
                })

                fetchReceivers(object: FetchReceiversCallback {
                    override fun onCallback(users: ArrayList<User>) {

                        fetchRooms(object: FetchRoomsCallback {
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onCallback(rooms: ArrayList<Room>) {

                                rooms.forEach { room ->
                                    if (room.lastMessage != "") {
                                        val receiverUid: String = if (room.members[0] == auth.currentUser!!.uid) {
                                            room.members[1]
                                        } else {
                                            room.members[0]
                                        }

                                        users.forEach { receiver ->
                                            if (receiver.uid == receiverUid) {
                                                receiver.username = contactsActivity.getContactName(this@Home, receiver.phoneNumber)!!

                                                conversationsArrayList.removeIf { it.room.id == room.id }
                                                searchConversationsArrayList.removeIf { it.room.id == room.id }

                                                conversationsArrayList.add(Conversation(room, sender, receiver))
                                            }
                                        }
                                    }
                                }

                                Log.e(TAG, "Conversations: ${conversationsArrayList.size}")
                                val sortedList = conversationsArrayList.sortedBy { it.room.lastUpdatedTimestamp }.toCollection(ArrayList())
                                searchConversationsArrayList.addAll(sortedList)
                                backupConversationsArrayList.addAll(sortedList)
                                conversationsArrayList.clear()
                                messagesAdapter.notifyDataSetChanged()
                                progressDialogActivity.dismissProgressDialog()
                            }
                        })
                    }
                })

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

    private fun fetchReceivers(fetchReceiversCallback: FetchReceiversCallback) {
        db.collection("users")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d(TAG, "Get failed with ", error)
                    return@addSnapshotListener
                }

                val usersArrayList: ArrayList<User> = arrayListOf()

                for (doc: DocumentChange in value?.documentChanges!!) {
                    if (doc.type == DocumentChange.Type.ADDED || doc.type == DocumentChange.Type.MODIFIED) {
                        val user: User = doc.document.toObject(User::class.java)
                        usersArrayList.add(user)
                    }
                }

                fetchReceiversCallback.onCallback(usersArrayList)
            }
    }

    private fun fetchRooms(fetchRoomsCallback: FetchRoomsCallback) {
        db.collection("rooms")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d(TAG, "Get failed with ", error)
                    return@addSnapshotListener
                }

                val roomsArrayList: ArrayList<Room> = arrayListOf()

                for (doc: DocumentChange in value?.documentChanges!!) {
                    if (doc.type == DocumentChange.Type.ADDED || doc.type == DocumentChange.Type.MODIFIED) {
                        val room: Room = doc.document.toObject(Room::class.java)

                        if (room.members[0] == auth.currentUser!!.uid || room.members[1] == auth.currentUser!!.uid) {
                            roomsArrayList.add(room)
                        }
                    }
                }

                fetchRoomsCallback.onCallback(roomsArrayList)
            }
    }

    private fun fetchSender(fetchSenderCallback: FetchSenderCallback) {
        val docRef = db.collection("users").document(auth.currentUser!!.uid)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                sender = snapshot.toObject<User>()!!
                fetchSenderCallback.onCallback(sender)
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }

    interface FetchRoomsCallback {
        fun onCallback(rooms: ArrayList<Room>)
    }

    interface FetchSenderCallback {
        fun onCallback(user: User)
    }

    interface FetchReceiversCallback {
        fun onCallback(users: ArrayList<User>)
    }

    private fun checkContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactsPermission() {
        val permission = arrayOf(android.Manifest.permission.READ_CONTACTS)
        ActivityCompat.requestPermissions(this, permission, contactPermissionCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == contactPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this, Contacts::class.java)
                intent.putExtra("SENDER", sender)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "Required permission denied", Toast.LENGTH_SHORT)
                    .show()

                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                this.startActivity(intent)
            }
        }
    }

    private fun setProfilePicture(profilePicture: String) {
        when (profilePicture) {
            "01" -> {
                profile.setImageResource(R.drawable.pp_1)
            }
            "02" -> {
                profile.setImageResource(R.drawable.pp_2)
            }
            "03" -> {
                profile.setImageResource(R.drawable.pp_3)
            }
            "04" -> {
                profile.setImageResource(R.drawable.pp_4)
            }
            "05" -> {
                profile.setImageResource(R.drawable.pp_5)
            }
            "06" -> {
                profile.setImageResource(R.drawable.pp_6)
            }
            "07" -> {
                profile.setImageResource(R.drawable.pp_7)
            }
            "08" -> {
                profile.setImageResource(R.drawable.pp_8)
            }
            "09" -> {
                profile.setImageResource(R.drawable.pp_9)
            }
            "10" -> {
                profile.setImageResource(R.drawable.pp_10)
            }
            "11" -> {
                profile.setImageResource(R.drawable.pp_11)
            }
            "12" -> {
                profile.setImageResource(R.drawable.pp_12)
            }
            "13" -> {
                profile.setImageResource(R.drawable.pp_13)
            }
            "14" -> {
                profile.setImageResource(R.drawable.pp_14)
            }
            "15" -> {
                profile.setImageResource(R.drawable.pp_15)
            }
            "16" -> {
                profile.setImageResource(R.drawable.pp_16)
            }
        }
    }

    private fun getFirstWord(fullName: String): String {
        return if (fullName.contains(" ")) {
            fullName.substring(0, fullName.indexOf(" "))
        } else {
            fullName
        }
    }

    private fun init() {
        contacts = findViewById(R.id.h_edit)
        profile = findViewById(R.id.h_profile)
        search = findViewById(R.id.h_search_view)
        greeting = findViewById(R.id.h_greeting)
        progressDialogActivity = WelcomeScreen()
        contactsActivity = Contacts()
        messagesRecyclerView = findViewById(R.id.h_recycler_view)
        conversationsArrayList = arrayListOf()
        searchConversationsArrayList = arrayListOf()
        backupConversationsArrayList = arrayListOf()
        noResults = findViewById(R.id.h_no_results_text)
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}