@file:Suppress("DEPRECATION")

package com.blackeyedghoul.cochat

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.viewpager2.widget.ViewPager2
import com.blackeyedghoul.cochat.adapters.ViewPagerAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class WelcomeScreen : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var next: CardView
    private lateinit var phoneNumber: TextInputEditText
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var progressDialog: ProgressDialog
    private var alertDialog: AlertDialog? = null
    private val sliderHandler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)

        init()

        auth = FirebaseAuth.getInstance()

        val images =
            listOf(R.drawable.welcome_pic_1, R.drawable.welcome_pic_2, R.drawable.welcome_pic_3)
        val adapter = ViewPagerAdapter(images, viewPager2)
        viewPager2.adapter = adapter

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000)
            }
        })

        window.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            window.decorView.getWindowVisibleDisplayFrame(r)

            val height = window.decorView.height
            if (height - r.bottom > height * 0.1399) {
                viewPager2.alpha = 0.0f
            } else {
                viewPager2.alpha = 1.0f
            }
        }

        phoneNumber.addTextChangedListener(signTextWatcher)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(applicationContext, Home::class.java))
            finish()
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: $credential")
                val intent = Intent(applicationContext, VerifyOtp::class.java)
                intent.putExtra("IS_ON_VERIFICATION_COMPLETED", true)
                intent.putExtra("CREDENTIALS", credential)
                intent.putExtra("PHONE_NUMBER", "+94".plus(phoneNumber.text.toString().trim()))
                startActivity(intent)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                dismissProgressDialog()
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG", "onCodeSent: $verificationId")

                storedVerificationId = verificationId
                resendToken = token

                dismissProgressDialog()

                val intent = Intent(applicationContext, VerifyOtp::class.java)
                intent.putExtra("VERIFICATION_ID", storedVerificationId)
                intent.putExtra("RESEND_TOKEN", resendToken)
                intent.putExtra("IS_ON_VERIFICATION_COMPLETED", false)
                intent.putExtra("PHONE_NUMBER", "+94".plus(phoneNumber.text.toString().trim()))
                startActivity(intent)
            }
        }

        checkNetworkConnection()
    }

    private var sliderRunnable = Runnable { viewPager2.currentItem = viewPager2.currentItem + 1 }

    private var signTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val phoneNumber = phoneNumber.text.toString().trim()

            if (phoneNumber.length == 9) {
                next.setCardBackgroundColor(Color.WHITE)
                next.isClickable = true
                next.isFocusable = true

                next.setOnClickListener {
                    showProgressDialog(this@WelcomeScreen)
                    sendVerificationCode(phoneNumber)
                }
            } else {
                next.setCardBackgroundColor(Color.parseColor("#FFAEC8FE"))
                next.isClickable = false
                next.isFocusable = false
            }
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+94".plus(phoneNumber))
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun showProgressDialog(context: Context?) {
        progressDialog = ProgressDialog(context)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progress_bar)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun dismissProgressDialog() {
        progressDialog.dismiss()
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

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    private fun init() {
        viewPager2 = findViewById(R.id.wl_view_pager)
        next = findViewById(R.id.wl_next_card)
        phoneNumber = findViewById(R.id.wl_phone_number_txt)
    }
}