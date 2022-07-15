package com.blackeyedghoul.cochat.models

import com.google.firebase.Timestamp

data class Chat(
    var id: String = "",
    var senderUid: String = "",
    var receiverUid: String = "",
    var timestamp: Timestamp = Timestamp.now(),
    var message: String = ""
)