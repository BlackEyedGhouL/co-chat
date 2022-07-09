package com.blackeyedghoul.cochat

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates

class SignUpProfilePicture : AppCompatActivity() {

    private lateinit var profilePicture01: ImageView
    private lateinit var profilePicture02: ImageView
    private lateinit var profilePicture03: ImageView
    private lateinit var profilePicture04: ImageView
    private lateinit var profilePicture05: ImageView
    private lateinit var profilePicture06: ImageView
    private lateinit var profilePicture07: ImageView
    private lateinit var profilePicture08: ImageView
    private lateinit var profilePicture09: ImageView
    private lateinit var profilePicture10: ImageView
    private lateinit var profilePicture11: ImageView
    private lateinit var profilePicture12: ImageView
    private lateinit var profilePicture13: ImageView
    private lateinit var profilePicture14: ImageView
    private lateinit var profilePicture15: ImageView
    private lateinit var profilePicture16: ImageView
    private lateinit var editProfilePicture01: CardView
    private lateinit var editProfilePicture02: CardView
    private lateinit var editProfilePicture03: CardView
    private lateinit var editProfilePicture04: CardView
    private lateinit var editProfilePicture05: CardView
    private lateinit var editProfilePicture06: CardView
    private lateinit var editProfilePicture07: CardView
    private lateinit var editProfilePicture08: CardView
    private lateinit var editProfilePicture09: CardView
    private lateinit var editProfilePicture10: CardView
    private lateinit var editProfilePicture11: CardView
    private lateinit var editProfilePicture12: CardView
    private lateinit var editProfilePicture13: CardView
    private lateinit var editProfilePicture14: CardView
    private lateinit var editProfilePicture15: CardView
    private lateinit var editProfilePicture16: CardView
    private lateinit var proceed: Button
    private var selectedImage: String = ""
    private lateinit var progressDialogActivity: WelcomeScreen
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_profile_picture)

        init()

        var value: Int by Delegates.observable(0) { _, _, newValue ->
            if (newValue == 1) {
                proceed.setBackgroundResource(R.drawable.btn_bg_proceed_enable)
                proceed.isClickable = true
                proceed.isFocusable = true

                proceed.setOnClickListener{
                    progressDialogActivity.showProgressDialog(this)
                    finalizeAccount()
                }
            }
            else {
                proceed.setBackgroundResource(R.drawable.btn_bg_proceed_disable)
                proceed.isClickable = false
                proceed.isFocusable = false
            }
        }

        profilePicture01.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture01.visibility == View.VISIBLE) {
                    editProfilePicture01.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture01.visibility = View.VISIBLE
                    value++
                    selectedImage = "01"
                }
            } else {
                if (editProfilePicture01.visibility == View.VISIBLE) {
                    editProfilePicture01.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture02.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture02.visibility == View.VISIBLE) {
                    editProfilePicture02.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture02.visibility = View.VISIBLE
                    value++
                    selectedImage = "02"
                }
            } else {
                if (editProfilePicture02.visibility == View.VISIBLE) {
                    editProfilePicture02.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture03.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture03.visibility == View.VISIBLE) {
                    editProfilePicture03.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture03.visibility = View.VISIBLE
                    value++
                    selectedImage = "03"
                }
            } else {
                if (editProfilePicture03.visibility == View.VISIBLE) {
                    editProfilePicture03.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture04.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture04.visibility == View.VISIBLE) {
                    editProfilePicture04.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture04.visibility = View.VISIBLE
                    value++
                    selectedImage = "04"
                }
            } else {
                if (editProfilePicture04.visibility == View.VISIBLE) {
                    editProfilePicture04.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture05.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture05.visibility == View.VISIBLE) {
                    editProfilePicture05.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture05.visibility = View.VISIBLE
                    value++
                    selectedImage = "05"
                }
            } else {
                if (editProfilePicture05.visibility == View.VISIBLE) {
                    editProfilePicture05.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture06.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture06.visibility == View.VISIBLE) {
                    editProfilePicture06.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture06.visibility = View.VISIBLE
                    value++
                    selectedImage = "06"
                }
            } else {
                if (editProfilePicture06.visibility == View.VISIBLE) {
                    editProfilePicture06.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture07.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture07.visibility == View.VISIBLE) {
                    editProfilePicture07.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture07.visibility = View.VISIBLE
                    value++
                    selectedImage = "07"
                }
            } else {
                if (editProfilePicture07.visibility == View.VISIBLE) {
                    editProfilePicture07.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture08.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture08.visibility == View.VISIBLE) {
                    editProfilePicture08.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture08.visibility = View.VISIBLE
                    value++
                    selectedImage = "08"
                }
            } else {
                if (editProfilePicture08.visibility == View.VISIBLE) {
                    editProfilePicture08.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture09.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture09.visibility == View.VISIBLE) {
                    editProfilePicture09.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture09.visibility = View.VISIBLE
                    value++
                    selectedImage = "09"
                }
            } else {
                if (editProfilePicture09.visibility == View.VISIBLE) {
                    editProfilePicture09.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture10.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture10.visibility == View.VISIBLE) {
                    editProfilePicture10.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture10.visibility = View.VISIBLE
                    value++
                    selectedImage = "10"
                }
            } else {
                if (editProfilePicture10.visibility == View.VISIBLE) {
                    editProfilePicture10.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture11.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture11.visibility == View.VISIBLE) {
                    editProfilePicture11.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture11.visibility = View.VISIBLE
                    value++
                    selectedImage = "11"
                }
            } else {
                if (editProfilePicture11.visibility == View.VISIBLE) {
                    editProfilePicture11.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture12.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture12.visibility == View.VISIBLE) {
                    editProfilePicture12.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture12.visibility = View.VISIBLE
                    value++
                    selectedImage = "12"
                }
            } else {
                if (editProfilePicture12.visibility == View.VISIBLE) {
                    editProfilePicture12.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture13.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture13.visibility == View.VISIBLE) {
                    editProfilePicture13.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture13.visibility = View.VISIBLE
                    value++
                    selectedImage = "13"
                }
            } else {
                if (editProfilePicture13.visibility == View.VISIBLE) {
                    editProfilePicture13.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture14.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture14.visibility == View.VISIBLE) {
                    editProfilePicture14.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture14.visibility = View.VISIBLE
                    value++
                    selectedImage = "14"
                }
            } else {
                if (editProfilePicture14.visibility == View.VISIBLE) {
                    editProfilePicture14.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture15.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture15.visibility == View.VISIBLE) {
                    editProfilePicture15.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture15.visibility = View.VISIBLE
                    value++
                    selectedImage = "15"
                }
            } else {
                if (editProfilePicture15.visibility == View.VISIBLE) {
                    editProfilePicture15.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profilePicture16.setOnClickListener{
            if (value == 0) {
                if (editProfilePicture16.visibility == View.VISIBLE) {
                    editProfilePicture16.visibility = View.GONE
                    value--
                } else {
                    editProfilePicture16.visibility = View.VISIBLE
                    value++
                    selectedImage = "16"
                }
            } else {
                if (editProfilePicture16.visibility == View.VISIBLE) {
                    editProfilePicture16.visibility = View.GONE
                    value--
                } else {
                    Toast.makeText(applicationContext, "You've already selected one", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun finalizeAccount() {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val name = intent.getStringExtra("NAME")

        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "username" to name.toString(),
            "profilePicture" to selectedImage,
            "joinedDate" to Timestamp.now(),
            "phoneNumber" to currentUser!!.phoneNumber
        )

        db.collection("users").document(currentUser.uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                progressDialogActivity.dismissProgressDialog()
                val intent = Intent(applicationContext, Home::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                progressDialogActivity.dismissProgressDialog()
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
    }

    private fun init() {
        profilePicture01 = findViewById(R.id.sp_pp_1)
        profilePicture02 = findViewById(R.id.sp_pp_2)
        profilePicture03 = findViewById(R.id.sp_pp_3)
        profilePicture04 = findViewById(R.id.sp_pp_4)
        profilePicture05 = findViewById(R.id.sp_pp_5)
        profilePicture06 = findViewById(R.id.sp_pp_6)
        profilePicture07 = findViewById(R.id.sp_pp_7)
        profilePicture08 = findViewById(R.id.sp_pp_8)
        profilePicture09 = findViewById(R.id.sp_pp_9)
        profilePicture10 = findViewById(R.id.sp_pp_10)
        profilePicture11 = findViewById(R.id.sp_pp_11)
        profilePicture12 = findViewById(R.id.sp_pp_12)
        profilePicture13 = findViewById(R.id.sp_pp_13)
        profilePicture14 = findViewById(R.id.sp_pp_14)
        profilePicture15 = findViewById(R.id.sp_pp_15)
        profilePicture16 = findViewById(R.id.sp_pp_16)
        editProfilePicture01 = findViewById(R.id.sp_selected_pp_1)
        editProfilePicture02 = findViewById(R.id.sp_selected_pp_2)
        editProfilePicture03 = findViewById(R.id.sp_selected_pp_3)
        editProfilePicture04 = findViewById(R.id.sp_selected_pp_4)
        editProfilePicture05 = findViewById(R.id.sp_selected_pp_5)
        editProfilePicture06 = findViewById(R.id.sp_selected_pp_6)
        editProfilePicture07 = findViewById(R.id.sp_selected_pp_7)
        editProfilePicture08 = findViewById(R.id.sp_selected_pp_8)
        editProfilePicture09 = findViewById(R.id.sp_selected_pp_9)
        editProfilePicture10 = findViewById(R.id.sp_selected_pp_10)
        editProfilePicture11 = findViewById(R.id.sp_selected_pp_11)
        editProfilePicture12 = findViewById(R.id.sp_selected_pp_12)
        editProfilePicture13 = findViewById(R.id.sp_selected_pp_13)
        editProfilePicture14 = findViewById(R.id.sp_selected_pp_14)
        editProfilePicture15 = findViewById(R.id.sp_selected_pp_15)
        editProfilePicture16 = findViewById(R.id.sp_selected_pp_16)
        proceed = findViewById(R.id.sp_proceed)
        progressDialogActivity = WelcomeScreen()
    }
}

//when (selectedImage) {
//    "01" -> {
//        uploadToStorage(R.drawable.pp_1)
//    }
//    "02" -> {
//        uploadToStorage(R.drawable.pp_2)
//    }
//    "03" -> {
//        uploadToStorage(R.drawable.pp_3)
//    }
//    "04" -> {
//        uploadToStorage(R.drawable.pp_4)
//    }
//    "05" -> {
//        uploadToStorage(R.drawable.pp_5)
//    }
//    "06" -> {
//        uploadToStorage(R.drawable.pp_6)
//    }
//    "07" -> {
//        uploadToStorage(R.drawable.pp_7)
//    }
//    "08" -> {
//        uploadToStorage(R.drawable.pp_8)
//    }
//    "09" -> {
//        uploadToStorage(R.drawable.pp_9)
//    }
//    "10" -> {
//        uploadToStorage(R.drawable.pp_10)
//    }
//    "11" -> {
//        uploadToStorage(R.drawable.pp_11)
//    }
//    "12" -> {
//        uploadToStorage(R.drawable.pp_12)
//    }
//    "13" -> {
//        uploadToStorage(R.drawable.pp_13)
//    }
//    "14" -> {
//        uploadToStorage(R.drawable.pp_14)
//    }
//    "15" -> {
//        uploadToStorage(R.drawable.pp_15)
//    }
//    "16" -> {
//        uploadToStorage(R.drawable.pp_16)
//    }
//}