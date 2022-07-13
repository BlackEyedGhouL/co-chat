package com.blackeyedghoul.cochat

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangeBioSheet(private val previousBio: String): BottomSheetDialogFragment() {

    private lateinit var bio: TextInputEditText
    private lateinit var done: Button
    private lateinit var back: ImageView
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.change_bio_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        bio.setText(previousBio)

        back.setOnClickListener{
            dismiss()
        }

        bio.addTextChangedListener(bioTextWatcher)
    }

    private fun updateUsername() {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(currentUser!!.uid)
            .update(
                "bio", bio.text.toString().trim()
            )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                dismiss()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error writing document", e)
                dismiss()
            }
    }

    private var bioTextWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val bio = bio.text.toString().trim()

            if (bio.isNotEmpty()) {
                done.setBackgroundResource(R.drawable.btn_bg_proceed_enable)
                done.isClickable = true
                done.isFocusable = true

                done.setOnClickListener{
                    updateUsername()
                }
            } else {
                done.setBackgroundResource(R.drawable.btn_bg_proceed_disable)
                done.isClickable = false
                done.isFocusable = false
            }
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    private fun init(view: View) {
        back = view.findViewById(R.id.cb_back)
        bio = view.findViewById(R.id.cb_bio_txt)
        done = view.findViewById(R.id.cb_done)
    }
}