package com.blackeyedghoul.cochat.models

import com.google.firebase.Timestamp

data class Conversation(
    var room: Room = Room("", listOf(), listOf(), null, ""),
    var sender: User = User("", "", "", Timestamp.now(), "", "", false, null),
    var receiver: User = User("", "", "", Timestamp.now(), "", "", false, null)
)