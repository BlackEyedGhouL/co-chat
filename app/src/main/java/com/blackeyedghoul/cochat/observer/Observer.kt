@file:Suppress("DEPRECATION")

package com.blackeyedghoul.cochat.observer

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.blackeyedghoul.cochat.models.User
import com.google.firebase.firestore.FirebaseFirestore

class Observer(private val sender: User): LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnteredForeground() {
        updateStatus(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnteredBackground() {
        updateStatus(false)
    }

    private fun updateStatus(status: Boolean) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(sender.uid)
            .update(
                "isOnline", status
            )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                return@addOnSuccessListener
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error writing document", e)
                return@addOnFailureListener
            }
    }
}