package com.blackeyedghoul.cochat.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    var id: String = "",
    var members: List<String> = listOf(), // <sender, receiver>
    @field:JvmField
    var isConversationStarterTyping: Boolean = false,
    @field:JvmField
    var isNonConversationStarterTyping: Boolean = false,
    var lastUpdatedTimestamp: Timestamp? = null,
    var lastMessage: String = "",
    var conversationStarterUid: String = ""
): Parcelable