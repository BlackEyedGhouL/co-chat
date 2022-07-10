package com.blackeyedghoul.cochat

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.blackeyedghoul.cochat.adapters.ContactsAdapter
import com.blackeyedghoul.cochat.models.User
import com.google.firebase.firestore.*

class Contacts : AppCompatActivity() {

    private lateinit var menu: ImageView
    private lateinit var search: ImageView
    private lateinit var back: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var usersArrayList: ArrayList<User>
    private lateinit var adapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        init()

        usersArrayList = arrayListOf()
        adapter = ContactsAdapter(usersArrayList)
        recyclerView.adapter = adapter
        fetchUsers()

        back.setOnClickListener{
            onBackPressed()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchUsers() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
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
            }
    }

    private fun init() {
        menu = findViewById(R.id.c_more)
        search = findViewById(R.id.c_search)
        back = findViewById(R.id.c_back)
        recyclerView = findViewById(R.id.c_recycler_view)
    }
}