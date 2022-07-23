package com.blackeyedghoul.cochat

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class SignUpUserName : AppCompatActivity() {

    private lateinit var name: TextInputEditText
    private lateinit var proceed: Button
    private var alertDialog: AlertDialog? = null
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_user_name)

        init()

        name.addTextChangedListener(nameTextWatcher)

        checkNetworkConnection()
    }

    private var nameTextWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val name = name.text.toString().trim()

            if (name.isNotEmpty()) {
                proceed.setBackgroundResource(R.drawable.btn_bg_proceed_enable)
                proceed.isClickable = true
                proceed.isFocusable = true

                proceed.setOnClickListener{
                    val intent = Intent(applicationContext, SignUpProfilePicture::class.java)
                    intent.putExtra("NAME", name)
                    startActivity(intent)
                }
            } else {
                proceed.setBackgroundResource(R.drawable.btn_bg_proceed_disable)
                proceed.isClickable = false
                proceed.isFocusable = false
            }
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
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

    private fun init() {
        name = findViewById(R.id.su_name_txt)
        proceed = findViewById(R.id.su_proceed)
    }
}