package com.blackeyedghoul.cochat.models

import com.google.firebase.Timestamp

data class Room(
    var id: String = "",
    var members: List<String> = listOf(), // <sender, receiver>
    @field:JvmField
    var isTyping: List<Boolean> = listOf(),
    var lastUpdatedTimestamp: Timestamp? = null,
    var lastMessage: String = "",
)