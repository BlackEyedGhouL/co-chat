package com.blackeyedghoul.cochat.models

import com.google.firebase.Timestamp

data class User(
    var username: String = "",
    var profilePicture: String = "",
    var phoneNumber: String = "",
    var joinedDate: Timestamp = Timestamp.now(),
    var bio: String = ""
)