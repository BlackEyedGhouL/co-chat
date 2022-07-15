package com.blackeyedghoul.cochat.models

import com.google.firebase.Timestamp

data class Room(
    var id: String = "",
    var members: List<String> = listOf(), // <sender, receiver>
    var isTyping: List<Boolean> = listOf(),
    var lastUpdatedTimestamp: Timestamp = Timestamp.now(),
    var lastMessage: String = "",
)