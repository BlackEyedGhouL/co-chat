package com.blackeyedghoul.cochat

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.blackeyedghoul.cochat.adapters.ContactsAdapter
import com.blackeyedghoul.cochat.models.User
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*


class Contacts : AppCompatActivity() {

    private lateinit var menu: ImageView
    private lateinit var back: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var usersArrayList: ArrayList<User>
    private lateinit var displayUsersArrayList: ArrayList<User>
    private lateinit var adapter: ContactsAdapter
    private lateinit var progressDialogActivity: WelcomeScreen
    private lateinit var search: SearchView

    @SuppressLint("DiscouragedPrivateApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        init()

        usersArrayList = arrayListOf()
        displayUsersArrayList = arrayListOf()
        adapter = ContactsAdapter(displayUsersArrayList, this)
        recyclerView.adapter = adapter
        progressDialogActivity.showProgressDialog(this)
        fetchUsers()

        menu.setOnClickListener{
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener{ item ->
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

        back.setOnClickListener{
            onBackPressed()
        }

        search.setOnClickListener{
            search.isIconified = false
        }

        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText!!.isNotEmpty()) {
                    displayUsersArrayList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    usersArrayList.forEach{
                        if (it.username.lowercase(Locale.getDefault()).contains(search)) {
                            displayUsersArrayList.add(it)
                        }
                    }

                    recyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    displayUsersArrayList.clear()
                    displayUsersArrayList.addAll(usersArrayList)
                    recyclerView.adapter!!.notifyDataSetChanged()
                }

                return true
            }
        })
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
                        Log.e(TAG, "ContactNumber: ".plus(tempPhoneNumber))
                        if(contactExists(this, tempPhoneNumber))
                            usersArrayList.add(user)
                    }
                }

                adapter.notifyDataSetChanged()
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
        recyclerView = findViewById(R.id.c_recycler_view)
        progressDialogActivity = WelcomeScreen()
    }
}