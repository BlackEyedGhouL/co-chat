@file:Suppress("DEPRECATION")

package com.blackeyedghoul.cochat.observer

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Observer: Application(), LifecycleObserver {

    private lateinit var auth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        auth = FirebaseAuth.getInstance()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.d(TAG, "App's in background")
        updateStatus(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.d(TAG, "App's in foreground")
        updateStatus(true)
    }

    private fun updateStatus(status: Boolean) {
        if (auth.currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(auth.currentUser!!.uid)
                .update(
                    "isOnline", status
                )
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    return@addOnSuccessListener
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error writing document", e)
                    return@addOnFailureListener
                }
        }
    }
}