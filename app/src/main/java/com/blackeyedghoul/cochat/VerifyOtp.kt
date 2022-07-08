package com.blackeyedghoul.cochat

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.blackeyedghoul.cochat.adapters.ViewPagerAdapter
import com.goodiebag.pinview.Pinview
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import me.relex.circleindicator.CircleIndicator3
import java.util.concurrent.TimeUnit

class VerifyOtp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewPager2: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var subtitle: TextView
    private lateinit var resend: TextView
    private var isOnVerificationCompleted: Boolean = false
    private lateinit var verificationId: String
    private lateinit var pinView: Pinview
    private lateinit var credentials: PhoneAuthCredential
    private lateinit var progressDialogActivity: WelcomeScreen
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        init()

        auth = FirebaseAuth.getInstance()

        val images = listOf(R.drawable.welcome_pic_1, R.drawable.welcome_pic_2, R.drawable.welcome_pic_3)
        val adapter = ViewPagerAdapter(images)
        viewPager2.adapter = adapter

        indicator.setViewPager(viewPager2)

        // Show and hide keyboard
        window.decorView.viewTreeObserver.addOnGlobalLayoutListener{
            val r = Rect()
            window.decorView.getWindowVisibleDisplayFrame(r)

            val height =window.decorView.height
            if(height - r.bottom>height*0.1399){
                viewPager2.alpha = 0.0f // Visible
                indicator.alpha = 0.0f
            }else{
                viewPager2.alpha = 1.0f // Invisible
                indicator.alpha = 1.0f
            }
        }

        isOnVerificationCompleted = intent.getBooleanExtra("IS_ON_VERIFICATION_COMPLETED", false)
        val phoneNumber = intent.getStringExtra("PHONE_NUMBER")

        subtitle.text = "Enter 6 digit number that sent to\n$phoneNumber"

        if (isOnVerificationCompleted) {
            credentials = intent.getParcelableExtra("CREDENTIALS")!!
            progressDialogActivity.showProgressDialog(this)
            signInWithPhoneAuthCredential(credentials)
        } else {
            verificationId = intent.getStringExtra("VERIFICATION_ID")!!
            resendToken = intent.getParcelableExtra("RESEND_TOKEN")!!
        }

        pinView.setPinViewEventListener{ pinView, _ ->
            val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                verificationId, pinView.value)
            progressDialogActivity.showProgressDialog(this)
            signInWithPhoneAuthCredential(credential)
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                progressDialogActivity.dismissProgressDialog()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialogActivity.dismissProgressDialog()
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                progressDialogActivity.dismissProgressDialog()
                Toast.makeText(applicationContext, "Resent OTP", Toast.LENGTH_LONG).show()
            }
        }

        resend.setOnClickListener{
            progressDialogActivity.showProgressDialog(this)
            resendVerificationCode(phoneNumber!!, resendToken)
        }
    }

    private fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressDialogActivity.dismissProgressDialog()
                    startActivity(Intent(this, Home::class.java))
                    finish()
                } else {
                    progressDialogActivity.dismissProgressDialog()
                    Toast.makeText(this,"Please check your OTP and try again",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun init() {
        viewPager2 = findViewById(R.id.vo_view_pager)
        indicator = findViewById(R.id.vo_indicator)
        subtitle = findViewById(R.id.vo_subtitle)
        pinView = findViewById(R.id.vo_pin_view)
        progressDialogActivity = WelcomeScreen()
        resend = findViewById(R.id.vo_resend)
    }
}