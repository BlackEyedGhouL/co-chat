package com.blackeyedghoul.cochat

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
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
                    Log.d(ContentValues.TAG, "Get failed with ", error)
                    return@addSnapshotListener
                }

                for (doc: DocumentChange in value?.documentChanges!!) {
                    if (doc.type == DocumentChange.Type.ADDED) {
                        usersArrayList.add(doc.document.toObject(User::class.java))
                    }
                }

                adapter.notifyDataSetChanged()
                displayUsersArrayList.addAll(usersArrayList)
                progressDialogActivity.dismissProgressDialog()
            }
    }

    private fun init() {
        menu = findViewById(R.id.c_more)
        search = findViewById(R.id.c_search_view)
        back = findViewById(R.id.c_back)
        recyclerView = findViewById(R.id.c_recycler_view)
        progressDialogActivity = WelcomeScreen()
    }
}