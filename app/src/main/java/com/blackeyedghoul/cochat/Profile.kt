package com.blackeyedghoul.cochat

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.blackeyedghoul.cochat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class Profile : AppCompatActivity() {

    private var alertDialog: AlertDialog? = null
    private lateinit var username: TextView
    private lateinit var phoneNumber: TextView
    private lateinit var bio: TextView
    private lateinit var profilePicture: ImageView
    private lateinit var back: ImageView
    private lateinit var edit: CardView
    private lateinit var progressDialogActivity: WelcomeScreen
    private lateinit var auth: FirebaseAuth
    private lateinit var user: User
    private lateinit var editUsername: ConstraintLayout
    private lateinit var editBio: ConstraintLayout
    private lateinit var changeUsernameSheet: ChangeUsernameSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        init()

        checkNetworkConnection()
        progressDialogActivity.showProgressDialog(this)
        getUserDataRealTime(currentUser!!.uid)

        val changeProfilePictureBottomSheet = ChangeProfilePictureSheet()
        edit.setOnClickListener{
            changeProfilePictureBottomSheet.show(supportFragmentManager, "ChangeProfilePictureSheet")
        }

        editUsername.setOnClickListener{
            changeUsernameSheet.show(supportFragmentManager, "ChangeUsernameSheet")
        }

        back.setOnClickListener{
            onBackPressed()
        }
    }

    private fun getUserDataRealTime(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(uid)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                progressDialogActivity.dismissProgressDialog()
                Log.w(TAG, "Listen failed.", e)
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {

                user = snapshot.toObject<User>()!!

                username.text = user.username
                bio.text = user.bio
                phoneNumber.text = user.phoneNumber
                setProfilePicture(user.profilePicture)
                changeUsernameSheet = ChangeUsernameSheet(user.username)

                progressDialogActivity.dismissProgressDialog()

                Log.d(TAG, "Current data: ${snapshot.data}")
            } else {
                progressDialogActivity.dismissProgressDialog()
                Log.d(TAG, "Current data: null")
                Toast.makeText(applicationContext, "Unknown user!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun init() {
        username = findViewById(R.id.p_username)
        bio = findViewById(R.id.p_bio)
        phoneNumber = findViewById(R.id.p_phone_number)
        edit = findViewById(R.id.p_edit_card)
        back = findViewById(R.id.p_back)
        profilePicture = findViewById(R.id.p_profile_picture)
        progressDialogActivity = WelcomeScreen()
        editUsername = findViewById(R.id.p_username_card)
        editBio = findViewById(R.id.p_bio_card)
    }

    private fun setProfilePicture(number: String) {
        when (number) {
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
                dismiss?.setOnClickListener{
                    alertDialog?.dismiss()
                }
            }
        }
    }
}