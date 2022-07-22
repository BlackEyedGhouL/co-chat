package com.blackeyedghoul.cochat.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    var id: String = "",
    var members: List<String> = listOf(), // <sender, receiver>
    @field:JvmField
    var isTyping: List<Boolean> = listOf(),
    var lastUpdatedTimestamp: Timestamp? = null,
    var lastMessage: String = "",
): Parcelable