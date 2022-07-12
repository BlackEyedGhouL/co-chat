package com.blackeyedghoul.cochat

import android.annotation.SuppressLint
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        init()

        val changeProfilePictureBottomSheet = ChangeProfilePictureSheet()
        edit.setOnClickListener{
            changeProfilePictureBottomSheet.show(supportFragmentManager, "ChangeProfilePictureSheet")
        }

        back.setOnClickListener{
            onBackPressed()
        }

        checkNetworkConnection()
        progressDialogActivity.showProgressDialog(this)
        getCurrentUserInfo(currentUser!!.uid)
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentUserInfo(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(uid)

        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                user = documentSnapshot.toObject<User>()!!

                username.text = user.username
                bio.text = user.bio
                phoneNumber.text = user.phoneNumber
                setProfilePicture(user.profilePicture)

                progressDialogActivity.dismissProgressDialog()
                Log.d(ContentValues.TAG, "DocumentSnapshot data: ${documentSnapshot.data}")
            }
            .addOnFailureListener { exception ->
                progressDialogActivity.dismissProgressDialog()
                Log.d(ContentValues.TAG, "Get failed with ", exception)
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
                Log.d(ContentValues.TAG, "NetworkConnection: true")
                alertDialog?.dismiss()
            } else {
                Log.d(ContentValues.TAG, "NetworkConnection: false")
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