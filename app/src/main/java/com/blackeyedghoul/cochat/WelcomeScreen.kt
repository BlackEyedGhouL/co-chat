package com.blackeyedghoul.cochat

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.blackeyedghoul.cochat.adapters.ViewPagerAdapter
import com.google.android.material.textfield.TextInputEditText
import me.relex.circleindicator.CircleIndicator3

class WelcomeScreen : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var next: CardView
    private lateinit var phoneNumber: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)

        init()

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

        phoneNumber.addTextChangedListener(signTextWatcher)
    }

    private var signTextWatcher = object: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val phoneNumber = phoneNumber.text.toString().trim()

            if (phoneNumber.length == 9) {
                next.setCardBackgroundColor(Color.WHITE)
                next.isClickable = true
                next.isFocusable = true

                next.setOnClickListener{
                    startActivity(Intent(this@WelcomeScreen, VerifyOtp::class.java).also { it.putExtra("PHONE_NUMBER", "+94".plus(phoneNumber)) })
                }
            } else {
                next.setCardBackgroundColor(Color.parseColor("#FFAEC8FE"))
                next.isClickable = false
                next.isFocusable = false
            }
        }

        override fun afterTextChanged(p0: Editable?) {}

    }

    private fun init() {
        viewPager2 = findViewById(R.id.wl_view_pager)
        indicator = findViewById(R.id.indicator)
        next = findViewById(R.id.wl_next_card)
        phoneNumber = findViewById(R.id.wl_phone_number_txt)
    }
}