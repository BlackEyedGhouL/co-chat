package com.blackeyedghoul.cochat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class SignUpUserName : AppCompatActivity() {

    private lateinit var name: TextInputEditText
    private lateinit var proceed: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_user_name)

        init()

        name.addTextChangedListener(nameTextWatcher)
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

    private fun init() {
        name = findViewById(R.id.su_name_txt)
        proceed = findViewById(R.id.su_proceed)
    }
}