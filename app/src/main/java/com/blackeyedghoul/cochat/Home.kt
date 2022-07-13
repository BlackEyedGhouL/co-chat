package com.blackeyedghoul.cochat

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blackeyedghoul.cochat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class Home : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: User
    private lateinit var contacts: ImageView
    private lateinit var profile: ImageView
    private lateinit var search: SearchView
    private lateinit var greeting: TextView
    private lateinit var progressDialogActivity: WelcomeScreen
    private val contactPermissionCode = 1
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        init()
        checkNetworkConnection()
        progressDialogActivity.showProgressDialog(this)
        getUserDataRealTime(currentUser!!.uid)

        contacts.setOnClickListener {
            if (checkContactsPermission()) {
                val intent = Intent(this, Contacts::class.java)
                startActivity(intent)
            } else {
                requestContactsPermission()
            }
        }

        profile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
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
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "Required permission denied", Toast.LENGTH_SHORT).show()

                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                this.startActivity(intent)
            }
        }
    }

    @SuppressLint("SetTextI18n")
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

                val firstName = getFirstWord(user.username)
                greeting.text = "Hello $firstName,"
                setProfilePicture(user.profilePicture)

                progressDialogActivity.dismissProgressDialog()
                Log.d(TAG, "Current data: ${snapshot.data}")
            } else {
                progressDialogActivity.dismissProgressDialog()
                Log.d(TAG, "Current data: null")
                Toast.makeText(applicationContext, "Unknown user!", Toast.LENGTH_SHORT).show()
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
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}