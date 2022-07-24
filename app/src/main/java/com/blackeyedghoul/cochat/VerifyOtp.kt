package com.blackeyedghoul.cochat

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.viewpager2.widget.ViewPager2
import com.blackeyedghoul.cochat.adapters.ViewPagerAdapter
import com.chaos.view.PinView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class VerifyOtp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewPager2: ViewPager2
    private lateinit var subtitle: TextView
    private lateinit var resend: TextView
    private var isOnVerificationCompleted: Boolean = false
    private lateinit var verificationId: String
    private lateinit var pinView: PinView
    private lateinit var credentials: PhoneAuthCredential
    private lateinit var progressDialogActivity: WelcomeScreen
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var alertDialog: AlertDialog? = null
    private val sliderHandler: Handler = Handler()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        init()

        auth = FirebaseAuth.getInstance()

        val images = listOf(R.drawable.welcome_pic_1, R.drawable.welcome_pic_2, R.drawable.welcome_pic_3)
        val adapter = ViewPagerAdapter(images, viewPager2)
        viewPager2.adapter = adapter

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 4000)
            }
        })

        window.decorView.viewTreeObserver.addOnGlobalLayoutListener{
            val r = Rect()
            window.decorView.getWindowVisibleDisplayFrame(r)

            val height =window.decorView.height
            if(height - r.bottom>height*0.1399){
                viewPager2.alpha = 0.0f
            }else{
                viewPager2.alpha = 1.0f
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

        pinView.setAnimationEnable(true)
        pinView.requestFocus()

        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        pinView.addTextChangedListener { pin ->
            if (pin.toString().length == 6) {
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    verificationId, pin.toString())
                progressDialogActivity.showProgressDialog(this)
                signInWithPhoneAuthCredential(credential)
            }
        }

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

        checkNetworkConnection()
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
                    checkExistingUserOrNot(task.result.user!!.uid)
                } else {
                    progressDialogActivity.dismissProgressDialog()
                    Toast.makeText(this,"Please check your OTP and try again",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkExistingUserOrNot(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(uid)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    progressDialogActivity.dismissProgressDialog()
                    startActivity(Intent(this, Home::class.java))
                    finish()
                } else {
                    Log.d(TAG, "No such document")
                    progressDialogActivity.dismissProgressDialog()
                    startActivity(Intent(this, SignUpUserName::class.java))
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Get failed with ", exception)
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

    private var sliderRunnable = Runnable { viewPager2.currentItem = viewPager2.currentItem + 1 }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 4000)
    }

    private fun init() {
        viewPager2 = findViewById(R.id.vo_view_pager)
        subtitle = findViewById(R.id.vo_subtitle)
        pinView = findViewById(R.id.vo_pin_view)
        progressDialogActivity = WelcomeScreen()
        resend = findViewById(R.id.vo_resend)
    }
}