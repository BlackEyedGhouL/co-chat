package com.blackeyedghoul.cochat.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var username: String = "",
    var profilePicture: String = "",
    var phoneNumber: String = "",
    var joinedDate: Timestamp = Timestamp.now(),
    var bio: String = "",
    var uid: String = "",
    @field:JvmField
    var isOnline: Boolean = false,
    var rooms: List<String>? = null,
    var fcmToken: String = ""
): Parcelable