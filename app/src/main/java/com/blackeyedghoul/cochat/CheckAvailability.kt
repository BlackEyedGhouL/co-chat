package com.blackeyedghoul.cochat

import android.content.ContentValues.TAG
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

open class CheckAvailability : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var docRef = FirebaseFirestore.getInstance().collection("users").document(auth.currentUser!!.uid)

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "CheckAvailability: offline")
        docRef.update("isOnline", false)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "CheckAvailability: online")
        docRef.update("isOnline", true)
    }
}