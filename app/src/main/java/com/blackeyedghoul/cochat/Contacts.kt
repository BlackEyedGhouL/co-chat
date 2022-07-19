package com.blackeyedghoul.cochat

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.blackeyedghoul.cochat.adapters.ContactsAdapter
import com.blackeyedghoul.cochat.adapters.InviteContactsAdapter
import com.blackeyedghoul.cochat.models.Contact
import com.blackeyedghoul.cochat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*


@Suppress("NAME_SHADOWING")
class Contacts : AppCompatActivity() {

    private lateinit var menu: ImageView
    private lateinit var back: ImageView
    private lateinit var recyclerViewContacts: RecyclerView
    private lateinit var recyclerViewInvite: RecyclerView
    private lateinit var usersArrayList: ArrayList<User>
    private lateinit var backupUsersArrayList: ArrayList<User>
    private lateinit var searchUsersArrayList: ArrayList<User>
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var inviteContactsAdapter: InviteContactsAdapter
    private lateinit var progressDialogActivity: WelcomeScreen
    private lateinit var search: SearchView
    private lateinit var contactList: ArrayList<Contact>
    private lateinit var alreadyUsersList: ArrayList<Contact>
    private lateinit var noResults: TextView
    private lateinit var inviteToChat: TextView
    private var alertDialog: AlertDialog? = null
    private lateinit var sender: User
    private lateinit var auth: FirebaseAuth

    @SuppressLint("DiscouragedPrivateApi", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        init()

        sender = intent.getParcelableExtra("SENDER")!!

        contactsAdapter = ContactsAdapter(searchUsersArrayList, this, sender)
        inviteContactsAdapter = InviteContactsAdapter(contactList, this)
        recyclerViewContacts.adapter = contactsAdapter
        recyclerViewInvite.adapter = inviteContactsAdapter

        checkNetworkConnection()

        menu.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.c_menu_invite -> {
                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            "Hey \uD83D\uDC4B\uD83C\uDFFC wanna try out CoChat? It's a simple, fast app we can use to chat. Get it at https://github.com/BlackEyedGhouL/co-chat"
                        )
                        intent.type = "text/plain"

                        startActivity(Intent.createChooser(intent, "Share"))
                        true
                    }
                    else -> false
                }
            }

            popupMenu.inflate(R.menu.contacts_menu)

            try {
                val fieldPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldPopup.isAccessible = true
                val mPopup = fieldPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e(TAG, "Ended with exception: ", e)
            } finally {
                popupMenu.show()
            }
        }

        back.setOnClickListener {
            onBackPressed()
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
                    searchUsersArrayList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    backupUsersArrayList.forEach {
                        if (it.username.lowercase(Locale.getDefault()).contains(search)) {
                            searchUsersArrayList.add(it)
                        }
                    }

                    recyclerViewContacts.adapter!!.notifyDataSetChanged()
                } else {
                    searchUsersArrayList.clear()
                    searchUsersArrayList.addAll(backupUsersArrayList)
                    recyclerViewContacts.adapter!!.notifyDataSetChanged()
                }

                if (contactsAdapter.itemCount == 0) {

                    if (newText.isNotEmpty())
                        noResults.text = "No results found for '$newText'"
                    else
                        noResults.text = "No contacts found"

                    noResults.visibility = View.VISIBLE
                } else {
                    noResults.visibility = View.GONE
                }

                return true
            }
        })
    }

    override fun onStart() {
        super.onStart()
        contactList.clear()
        searchUsersArrayList.clear()
        usersArrayList.clear()
    }

    private fun checkNetworkConnection() {
        val networkConnection = InternetConnection(this)
        networkConnection.observe(this) { isConnected ->

            val view = View.inflate(this, R.layout.no_internet_alert, null)
            val builder = AlertDialog.Builder(this, R.style.FullscreenAlertDialog)
            builder.setView(view)

            progressDialogActivity.showProgressDialog(this)

            if (isConnected) {
                Log.d(TAG, "NetworkConnection: true")
                alertDialog?.dismiss()
                getContactList()
                fetchUsers()
            } else {
                Log.d(TAG, "NetworkConnection: false")
                progressDialogActivity.dismissProgressDialog()
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

    private val projection = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    private fun getContactList() {
        val cr = contentResolver
        val cursor = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor != null) {
            val mobileNoSet = HashSet<String>()
            cursor.use { cursor ->
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val numberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                var name: String
                var number: String
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex)
                    number = cursor.getString(numberIndex)
                    number = number.replace(" ", "")
                    if (!mobileNoSet.contains(number)) {
                        contactList.add(Contact(name, number))
                        mobileNoSet.add(number)
                        userExists(name, number)
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun userExists(name: String?, number: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d(TAG, "Get failed with ", error)
                    return@addSnapshotListener
                }

                for (doc: DocumentChange in value?.documentChanges!!) {
                    if (doc.type == DocumentChange.Type.ADDED) {
                        val user: User = doc.document.toObject(User::class.java)
                        val contact = Contact(name!!, number)

                        if (user.phoneNumber == contact.phoneNumber || convertPhoneNumber(user.phoneNumber) == contact.phoneNumber) {
                            contactList.remove(contact)
                        }
                    }
                }
                inviteContactsAdapter.notifyDataSetChanged()
            }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "NewApi")
    private fun fetchUsers() {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .orderBy("username", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d(TAG, "Get failed with ", error)
                    return@addSnapshotListener
                }

                for (doc: DocumentChange in value?.documentChanges!!) {
                    if (doc.type == DocumentChange.Type.ADDED) {
                        val user: User = doc.document.toObject(User::class.java)
                        val tempPhoneNumber = convertPhoneNumber(user.phoneNumber)
                        val method1: Boolean = contactExists(this, tempPhoneNumber)
                        val method2: Boolean = contactExists(this, user.phoneNumber)
                        if (method1 || method2) {
                            var name: String = user.username
                            if (method1) {
                                name = getContactName(this, tempPhoneNumber)!!
                            } else if (method2) {
                                name = getContactName(this, user.phoneNumber)!!
                            } else if (method1 && method2) {
                                name = getContactName(this, tempPhoneNumber)!!
                            }
                            user.username = name

                            if (currentUser!!.phoneNumber != user.phoneNumber && currentUser.phoneNumber != tempPhoneNumber)
                                usersArrayList.add(user)
                        }
                    } else if (doc.type == DocumentChange.Type.MODIFIED) {
                        val user: User = doc.document.toObject(User::class.java)
                        val tempPhoneNumber = convertPhoneNumber(user.phoneNumber)
                        val method1: Boolean = contactExists(this, tempPhoneNumber)
                        val method2: Boolean = contactExists(this, user.phoneNumber)
                        if (method1 || method2) {
                            var name: String = user.username
                            if (method1) {
                                name = getContactName(this, tempPhoneNumber)!!
                            } else if (method2) {
                                name = getContactName(this, user.phoneNumber)!!
                            } else if (method1 && method2) {
                                name = getContactName(this, tempPhoneNumber)!!
                            }
                            user.username = name

                            usersArrayList.removeIf { it.phoneNumber == user.phoneNumber }
                            searchUsersArrayList.removeIf { it.phoneNumber == user.phoneNumber }

                            if (currentUser!!.phoneNumber != user.phoneNumber && currentUser.phoneNumber != tempPhoneNumber)
                                usersArrayList.add(user)
                        }
                    }
                }

                val sortedList = usersArrayList.sortedBy { it.username }.toCollection(ArrayList())
                searchUsersArrayList.addAll(sortedList)
                backupUsersArrayList.addAll(sortedList)
                usersArrayList.clear()
                contactsAdapter.notifyDataSetChanged()

                if (contactsAdapter.itemCount == 0) {
                    noResults.text = "No contacts found"
                    noResults.visibility = View.VISIBLE
                } else {
                    noResults.visibility = View.GONE
                }

                progressDialogActivity.dismissProgressDialog()
            }
    }

    @SuppressLint("Range")
    fun getContactName(context: Context, phoneNumber: String?): String? {
        val cr = context.contentResolver
        val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        val cursor = cr.query(uri, arrayOf(PhoneLookup.DISPLAY_NAME), null, null, null)
            ?: return null
        var contactName: String? = null
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME))
        }
        if (!cursor.isClosed) {
            cursor.close()
        }
        return contactName
    }

    @SuppressLint("Range", "Recycle")
    private fun contactExists(context: Context, number: String?): Boolean {
        return if (number != null) {
            val cr: ContentResolver = context.contentResolver
            val curContacts: Cursor? =
                cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
            if (curContacts != null) {
                while (curContacts.moveToNext()) {
                    val contactNumber: String =
                        curContacts.getString(curContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    if (number == contactNumber) {
                        return true
                    }
                }
            }
            false
        } else {
            false
        }
    }

    private fun convertPhoneNumber(phoneNumber: String): String {
        return "0".plus(phoneNumber.substring(3, phoneNumber.lastIndex + 1))
    }

    private fun init() {
        menu = findViewById(R.id.c_more)
        search = findViewById(R.id.c_search_view)
        back = findViewById(R.id.c_back)
        recyclerViewContacts = findViewById(R.id.c_contacts_recycler_view)
        recyclerViewInvite = findViewById(R.id.c_invite_recycler_view)
        progressDialogActivity = WelcomeScreen()
        usersArrayList = arrayListOf()
        searchUsersArrayList = arrayListOf()
        contactList = arrayListOf()
        alreadyUsersList = arrayListOf()
        backupUsersArrayList = arrayListOf()
        noResults = findViewById(R.id.c_no_results_text)
        inviteToChat = findViewById(R.id.c_invite_text)
    }
}