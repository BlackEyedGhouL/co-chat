package com.blackeyedghoul.cochat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlin.math.log

class VerifyOtp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        val phoneNumber = intent.getStringExtra("PHONE_NUMBER")
    }
}