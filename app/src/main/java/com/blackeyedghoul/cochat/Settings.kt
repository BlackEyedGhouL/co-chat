package com.blackeyedghoul.cochat

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class Settings : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var notificationSwitch: SwitchMaterial
    private lateinit var logOut: ConstraintLayout
    private lateinit var chatBackground: ConstraintLayout
    private lateinit var progressDialogActivity: WelcomeScreen
    private lateinit var configuration: com.blackeyedghoul.cochat.models.Configuration
    private var alertDialog: AlertDialog? = null
    private val db = FirebaseFirestore.getInstance()
    private lateinit var back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        init()

        logOut.setOnClickListener{
            updateStatus()
        }

        back.setOnClickListener {
            onBackPressed()
        }

        checkNetworkConnection()
        progressDialogActivity.showProgressDialog(this)
        checkPushNotifications(currentUser!!.uid)

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            updatePushNotifications(currentUser.uid, isChecked)
        }
    }

    private fun updateStatus() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(auth.currentUser!!.uid)
            .update(
                "isOnline", false
            )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                auth.signOut()
                val intent = Intent(this, WelcomeScreen::class.java)
                startActivity(intent)
                finishAffinity()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error writing document", e)
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }

    private fun updatePushNotifications(uid: String, isChecked: Boolean) {
        db.collection("settings").document(uid)
            .update(
                "isPushNotificationEnabled", isChecked
            )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                return@addOnSuccessListener
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error writing document", e)
                return@addOnFailureListener
            }
    }

    private fun checkPushNotifications(uid: String) {
        val docRef = db.collection("settings").document(uid)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                progressDialogActivity.dismissProgressDialog()
                Log.w(ContentValues.TAG, "Listen failed.", e)
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                configuration = snapshot.toObject<com.blackeyedghoul.cochat.models.Configuration>()!!

                notificationSwitch.isChecked = configuration.isPushNotificationEnabled

                progressDialogActivity.dismissProgressDialog()
                Log.d(ContentValues.TAG, "Current data: ${snapshot.data}")
            } else {
                progressDialogActivity.dismissProgressDialog()
                Log.d(ContentValues.TAG, "Current data: null")
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

                chatBackground.setOnClickListener{
                    val intent = Intent(this, ChatBackground::class.java)
                    intent.putExtra("CHAT_BACKGROUND", configuration.chatBackground)
                    startActivity(intent)
                }

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

                chatBackground.setOnClickListener{
                    Toast.makeText(applicationContext, "Please try again when there's an active internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun init() {
        notificationSwitch = findViewById(R.id.s_notifications_switch)
        logOut = findViewById(R.id.s_log_out_card)
        chatBackground = findViewById(R.id.s_chat_background_card)
        progressDialogActivity = WelcomeScreen()
        back = findViewById(R.id.s_back)
    }
}