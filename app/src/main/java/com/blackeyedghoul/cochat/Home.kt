package com.blackeyedghoul.cochat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Home : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        Toast.makeText(this,currentUser!!.phoneNumber, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}