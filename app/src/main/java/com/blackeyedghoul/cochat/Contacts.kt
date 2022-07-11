package com.blackeyedghoul.cochat

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.blackeyedghoul.cochat.adapters.ContactsAdapter
import com.blackeyedghoul.cochat.adapters.InviteContactsAdapter
import com.blackeyedghoul.cochat.models.Contact
import com.blackeyedghoul.cochat.models.User
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*


class Contacts : AppCompatActivity() {

    private lateinit var menu: ImageView
    private lateinit var back: ImageView
    private lateinit var recyclerViewContacts: RecyclerView
    private lateinit var recyclerViewInvite: RecyclerView
    private lateinit var usersArrayList: ArrayList<User>
    private lateinit var displayUsersArrayList: ArrayList<User>
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var inviteContactsAdapter: InviteContactsAdapter
    private lateinit var progressDialogActivity: WelcomeScreen
    private lateinit var search: SearchView
    private lateinit var contactList: ArrayList<Contact>
    private lateinit var inviteContactList: ArrayList<Contact>
    private lateinit var noResults: TextView

    @SuppressLint("DiscouragedPrivateApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        init()

        contactsAdapter = ContactsAdapter(displayUsersArrayList, this)
        inviteContactsAdapter = InviteContactsAdapter(inviteContactList, this)
        recyclerViewContacts.adapter = contactsAdapter
        recyclerViewInvite.adapter = inviteContactsAdapter
        progressDialogActivity.showProgressDialog(this)
        getContactList()
        fetchUsers()

        menu.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.c_menu_invite -> {
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
                    displayUsersArrayList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    usersArrayList.forEach {
                        if (it.username.lowercase(Locale.getDefault()).contains(search)) {
                            displayUsersArrayList.add(it)
                        }
                    }

                    recyclerViewContacts.adapter!!.notifyDataSetChanged()
                } else {
                    displayUsersArrayList.clear()
                    displayUsersArrayList.addAll(usersArrayList)
                    recyclerViewContacts.adapter!!.notifyDataSetChanged()
                }

                if (contactsAdapter.itemCount == 0) {
                    noResults.text = "No results found in '$newText'"
                    noResults.visibility = View.VISIBLE
                } else {
                    noResults.visibility = View.GONE
                }

                return true
            }
        })
    }

    private val PROJECTION = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    private fun getContactList() {
        val cr = contentResolver
        val cursor = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PROJECTION,
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
                        Log.d(TAG, "Phone Number: Name = $name || Number = $number")

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
            .orderBy("username", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d(TAG, "Get failed with ", error)
                    return@addSnapshotListener
                }

                for (doc: DocumentChange in value?.documentChanges!!) {
                    if (doc.type == DocumentChange.Type.ADDED) {
                        val user: User = doc.document.toObject(User::class.java)
                        val contact = Contact(name!!, number)

                        if (!inviteContactList.contains(contact)) {
                            if (user.phoneNumber.substring(0, 2) == "+94") {
                                val tempPhoneNumber = convertPhoneNumber(user.phoneNumber)
                                if (number != tempPhoneNumber && number != user.phoneNumber) {
                                    inviteContactList.add(contact)
                                }
                            } else {
                                if (number != user.phoneNumber) {
                                    inviteContactList.add(contact)
                                }
                            }
                            Log.d(TAG, "Invite : Name = $name || Number = $number")
                        }
                    }
                }

                inviteContactsAdapter.notifyDataSetChanged()
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchUsers() {
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
                        if (contactExists(this, tempPhoneNumber) || contactExists(
                                this,
                                user.phoneNumber
                            )
                        )
                            usersArrayList.add(user)
                    }
                }

                contactsAdapter.notifyDataSetChanged()
                displayUsersArrayList.addAll(usersArrayList)
                progressDialogActivity.dismissProgressDialog()
            }
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
        displayUsersArrayList = arrayListOf()
        contactList = arrayListOf()
        inviteContactList = arrayListOf()
        noResults = findViewById(R.id.c_no_results_text)
    }
}