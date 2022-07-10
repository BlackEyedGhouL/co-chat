package com.blackeyedghoul.cochat

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.blackeyedghoul.cochat.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class Home : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: User
    private lateinit var contacts: ImageView
    private lateinit var profile: ImageView
    private lateinit var search: TextInputEditText
    private lateinit var greeting: TextView
    private lateinit var progressDialogActivity: WelcomeScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        init()
        progressDialogActivity.showProgressDialog(this)
        getCurrentUserInfo(currentUser!!.uid)

        search.addTextChangedListener(searchTextWatcher)

        contacts.setOnClickListener {
            val intent = Intent(this, Contacts::class.java)
            startActivity(intent)
        }

        profile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }
    }

    private var searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentUserInfo(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(uid)

        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                user = documentSnapshot.toObject<User>()!!

                val firstName = getFirstWord(user.username)
                greeting.text = "Hello $firstName,"

                setProfilePicture(user.profilePicture)

                progressDialogActivity.dismissProgressDialog()
                Log.d(TAG, "DocumentSnapshot data: ${documentSnapshot.data}")
            }
            .addOnFailureListener { exception ->
                progressDialogActivity.dismissProgressDialog()
                Log.d(TAG, "Get failed with ", exception)
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
        return fullName.substring(0, fullName.indexOf(" "))
    }

    private fun init() {
        contacts = findViewById(R.id.h_edit)
        profile = findViewById(R.id.h_profile)
        search = findViewById(R.id.h_search_txt)
        greeting = findViewById(R.id.h_greeting)
        progressDialogActivity = WelcomeScreen()
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}