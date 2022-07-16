package com.blackeyedghoul.cochat

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatBackground : AppCompatActivity() {

    private var alertDialog: AlertDialog? = null
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var background01: ConstraintLayout
    private lateinit var background02: ConstraintLayout
    private lateinit var background03: ConstraintLayout
    private lateinit var background04: ConstraintLayout
    private lateinit var background05: ConstraintLayout
    private lateinit var background06: ConstraintLayout
    private lateinit var background07: ConstraintLayout
    private lateinit var background08: ConstraintLayout
    private lateinit var background09: ConstraintLayout
    private lateinit var backgroundIcon01: ImageView
    private lateinit var backgroundIcon02: ImageView
    private lateinit var backgroundIcon03: ImageView
    private lateinit var backgroundIcon04: ImageView
    private lateinit var backgroundIcon05: ImageView
    private lateinit var backgroundIcon06: ImageView
    private lateinit var backgroundIcon07: ImageView
    private lateinit var backgroundIcon08: ImageView
    private lateinit var backgroundIcon09: ImageView
    private lateinit var backgroundCover01: CardView
    private lateinit var backgroundCover02: CardView
    private lateinit var backgroundCover03: CardView
    private lateinit var backgroundCover04: CardView
    private lateinit var backgroundCover05: CardView
    private lateinit var backgroundCover06: CardView
    private lateinit var backgroundCover07: CardView
    private lateinit var backgroundCover08: CardView
    private lateinit var backgroundCover09: CardView
    private var selectedImage: String = ""
    private lateinit var progressDialogActivity: WelcomeScreen
    private lateinit var back: ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_background)

        init()
        checkNetworkConnection()

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val chatBackground = intent.getStringExtra("CHAT_BACKGROUND")!!
        progressDialogActivity.showProgressDialog(this)
        setSelectedBackground(chatBackground)
        progressDialogActivity.dismissProgressDialog()

        back.setOnClickListener {
            onBackPressed()
        }

        background01.setOnClickListener {
            if (selectedImage == "01") {
                Toast.makeText(this, "You can't unselect the default background", Toast.LENGTH_SHORT).show()
            } else {
                if (selectedImage == "") {
                    backgroundIcon01.visibility = View.VISIBLE
                    backgroundCover01.visibility = View.VISIBLE
                    selectedImage = "01"
                    progressDialogActivity.showProgressDialog(this)
                    updateSelectedBackground(selectedImage, currentUser!!.uid)
                } else {
                    Toast.makeText(this, "You've already selected one, unselect it and try again", Toast.LENGTH_SHORT).show()
                }
            }
        }

        background02.setOnClickListener {
            if (selectedImage == "02") {
                backgroundIcon02.visibility = View.INVISIBLE
                backgroundCover02.visibility = View.INVISIBLE
                selectedImage = ""
            } else {
                if (selectedImage == "" || selectedImage == "01") {
                    backgroundIcon01.visibility = View.INVISIBLE
                    backgroundCover01.visibility = View.INVISIBLE
                    backgroundIcon02.visibility = View.VISIBLE
                    backgroundCover02.visibility = View.VISIBLE
                    selectedImage = "02"
                    progressDialogActivity.showProgressDialog(this)
                    updateSelectedBackground(selectedImage, currentUser!!.uid)
                } else {
                    Toast.makeText(this, "You've already selected one, unselect it and try again", Toast.LENGTH_SHORT).show()
                }
            }
        }

        background03.setOnClickListener {
            if (selectedImage == "03") {
                backgroundIcon03.visibility = View.INVISIBLE
                backgroundCover03.visibility = View.INVISIBLE
                selectedImage = ""
            } else {
                if (selectedImage == "" || selectedImage == "01") {
                    backgroundIcon01.visibility = View.INVISIBLE
                    backgroundCover01.visibility = View.INVISIBLE
                    backgroundIcon03.visibility = View.VISIBLE
                    backgroundCover03.visibility = View.VISIBLE
                    selectedImage = "03"
                    progressDialogActivity.showProgressDialog(this)
                    updateSelectedBackground(selectedImage, currentUser!!.uid)
                } else {
                    Toast.makeText(this, "You've already selected one, unselect it and try again", Toast.LENGTH_SHORT).show()
                }
            }
        }

        background04.setOnClickListener {
            if (selectedImage == "04") {
                backgroundIcon04.visibility = View.INVISIBLE
                backgroundCover04.visibility = View.INVISIBLE
                selectedImage = ""
            } else {
                if (selectedImage == "" || selectedImage == "01") {
                    backgroundIcon01.visibility = View.INVISIBLE
                    backgroundCover01.visibility = View.INVISIBLE
                    backgroundIcon04.visibility = View.VISIBLE
                    backgroundCover04.visibility = View.VISIBLE
                    selectedImage = "04"
                    progressDialogActivity.showProgressDialog(this)
                    updateSelectedBackground(selectedImage, currentUser!!.uid)
                } else {
                    Toast.makeText(this, "You've already selected one, unselect it and try again", Toast.LENGTH_SHORT).show()
                }
            }
        }

        background05.setOnClickListener {
            if (selectedImage == "05") {
                backgroundIcon05.visibility = View.INVISIBLE
                backgroundCover05.visibility = View.INVISIBLE
                selectedImage = ""
            } else {
                if (selectedImage == "" || selectedImage == "01") {
                    backgroundIcon01.visibility = View.INVISIBLE
                    backgroundCover01.visibility = View.INVISIBLE
                    backgroundIcon05.visibility = View.VISIBLE
                    backgroundCover05.visibility = View.VISIBLE
                    selectedImage = "05"
                    progressDialogActivity.showProgressDialog(this)
                    updateSelectedBackground(selectedImage, currentUser!!.uid)
                } else {
                    Toast.makeText(this, "You've already selected one, unselect it and try again", Toast.LENGTH_SHORT).show()
                }
            }
        }

        background06.setOnClickListener {
            if (selectedImage == "06") {
                backgroundIcon06.visibility = View.INVISIBLE
                backgroundCover06.visibility = View.INVISIBLE
                selectedImage = ""
            } else {
                if (selectedImage == "" || selectedImage == "01") {
                    backgroundIcon01.visibility = View.INVISIBLE
                    backgroundCover01.visibility = View.INVISIBLE
                    backgroundIcon06.visibility = View.VISIBLE
                    backgroundCover06.visibility = View.VISIBLE
                    selectedImage = "06"
                    progressDialogActivity.showProgressDialog(this)
                    updateSelectedBackground(selectedImage, currentUser!!.uid)
                } else {
                    Toast.makeText(this, "You've already selected one, unselect it and try again", Toast.LENGTH_SHORT).show()
                }
            }
        }

        background07.setOnClickListener {
            if (selectedImage == "07") {
                backgroundIcon07.visibility = View.INVISIBLE
                backgroundCover07.visibility = View.INVISIBLE
                selectedImage = ""
            } else {
                if (selectedImage == "" || selectedImage == "01") {
                    backgroundIcon01.visibility = View.INVISIBLE
                    backgroundCover01.visibility = View.INVISIBLE
                    backgroundIcon07.visibility = View.VISIBLE
                    backgroundCover07.visibility = View.VISIBLE
                    selectedImage = "07"
                    progressDialogActivity.showProgressDialog(this)
                    updateSelectedBackground(selectedImage, currentUser!!.uid)
                } else {
                    Toast.makeText(this, "You've already selected one, unselect it and try again", Toast.LENGTH_SHORT).show()
                }
            }
        }

        background08.setOnClickListener {
            if (selectedImage == "08") {
                backgroundIcon08.visibility = View.INVISIBLE
                backgroundCover08.visibility = View.INVISIBLE
                selectedImage = ""
            } else {
                if (selectedImage == "" || selectedImage == "01") {
                    backgroundIcon01.visibility = View.INVISIBLE
                    backgroundCover01.visibility = View.INVISIBLE
                    backgroundIcon08.visibility = View.VISIBLE
                    backgroundCover08.visibility = View.VISIBLE
                    selectedImage = "08"
                    progressDialogActivity.showProgressDialog(this)
                    updateSelectedBackground(selectedImage, currentUser!!.uid)
                } else {
                    Toast.makeText(this, "You've already selected one, unselect it and try again", Toast.LENGTH_SHORT).show()
                }
            }
        }

        background09.setOnClickListener {
            if (selectedImage == "09") {
                backgroundIcon09.visibility = View.INVISIBLE
                backgroundCover09.visibility = View.INVISIBLE
                selectedImage = ""
            } else {
                if (selectedImage == "" || selectedImage == "01") {
                    backgroundIcon01.visibility = View.INVISIBLE
                    backgroundCover01.visibility = View.INVISIBLE
                    backgroundIcon09.visibility = View.VISIBLE
                    backgroundCover09.visibility = View.VISIBLE
                    selectedImage = "09"
                    progressDialogActivity.showProgressDialog(this)
                    updateSelectedBackground(selectedImage, currentUser!!.uid)
                } else {
                    Toast.makeText(this, "You've already selected one, unselect it and try again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateSelectedBackground(selectedImage: String, uid: String) {
        db.collection("settings").document(uid)
            .update(
                "chatBackground", selectedImage
            )
            .addOnSuccessListener {
                progressDialogActivity.dismissProgressDialog()
                Toast.makeText(this, "Background set!", Toast.LENGTH_SHORT).show()
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                return@addOnSuccessListener
            }
            .addOnFailureListener { e ->
                progressDialogActivity.dismissProgressDialog()
                Toast.makeText(this, "Background set failed!", Toast.LENGTH_SHORT).show()
                Log.w(ContentValues.TAG, "Error writing document", e)
                return@addOnFailureListener
            }
    }

    private fun setSelectedBackground(chatBackground: String) {
        when (chatBackground) {
            "01" -> {
                selectedImage = "01"
                backgroundIcon01.visibility = View.VISIBLE
                backgroundCover01.visibility = View.VISIBLE
            }
            "02" -> {
                selectedImage = "02"
                backgroundIcon02.visibility = View.VISIBLE
                backgroundCover02.visibility = View.VISIBLE
            }
            "03" -> {
                selectedImage = "03"
                backgroundIcon03.visibility = View.VISIBLE
                backgroundCover03.visibility = View.VISIBLE
            }
            "04" -> {
                selectedImage = "04"
                backgroundIcon04.visibility = View.VISIBLE
                backgroundCover04.visibility = View.VISIBLE
            }
            "05" -> {
                selectedImage = "05"
                backgroundIcon05.visibility = View.VISIBLE
                backgroundCover05.visibility = View.VISIBLE
            }
            "06" -> {
                selectedImage = "06"
                backgroundIcon06.visibility = View.VISIBLE
                backgroundCover06.visibility = View.VISIBLE
            }
            "07" -> {
                selectedImage = "07"
                backgroundIcon07.visibility = View.VISIBLE
                backgroundCover07.visibility = View.VISIBLE
            }
            "08" -> {
                selectedImage = "08"
                backgroundIcon08.visibility = View.VISIBLE
                backgroundCover08.visibility = View.VISIBLE
            }
            "09" -> {
                selectedImage = "09"
                backgroundIcon09.visibility = View.VISIBLE
                backgroundCover09.visibility = View.VISIBLE
            }
        }
    }

    private fun init() {
        back = findViewById(R.id.c_back_back)
        progressDialogActivity = WelcomeScreen()
        background01 = findViewById(R.id.c_back_01)
        background02 = findViewById(R.id.c_back_02)
        background03 = findViewById(R.id.c_back_03)
        background04 = findViewById(R.id.c_back_04)
        background05 = findViewById(R.id.c_back_05)
        background06 = findViewById(R.id.c_back_06)
        background07 = findViewById(R.id.c_back_07)
        background08 = findViewById(R.id.c_back_08)
        background09 = findViewById(R.id.c_back_09)
        backgroundIcon01 = findViewById(R.id.c_back_selected_img_pp_1)
        backgroundIcon02 = findViewById(R.id.c_back_selected_img_pp_2)
        backgroundIcon03 = findViewById(R.id.c_back_selected_img_pp_3)
        backgroundIcon04 = findViewById(R.id.c_back_selected_img_pp_4)
        backgroundIcon05 = findViewById(R.id.c_back_selected_img_pp_5)
        backgroundIcon06 = findViewById(R.id.c_back_selected_img_pp_6)
        backgroundIcon07 = findViewById(R.id.c_back_selected_img_pp_7)
        backgroundIcon08 = findViewById(R.id.c_back_selected_img_pp_8)
        backgroundIcon09 = findViewById(R.id.c_back_selected_img_pp_9)
        backgroundCover01 = findViewById(R.id.c_back_selected_cb_1)
        backgroundCover02 = findViewById(R.id.c_back_selected_cb_2)
        backgroundCover03 = findViewById(R.id.c_back_selected_cb_3)
        backgroundCover04 = findViewById(R.id.c_back_selected_cb_4)
        backgroundCover05 = findViewById(R.id.c_back_selected_cb_5)
        backgroundCover06 = findViewById(R.id.c_back_selected_cb_6)
        backgroundCover07 = findViewById(R.id.c_back_selected_cb_7)
        backgroundCover08 = findViewById(R.id.c_back_selected_cb_8)
        backgroundCover09 = findViewById(R.id.c_back_selected_cb_9)
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