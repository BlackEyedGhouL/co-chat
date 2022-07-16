package com.blackeyedghoul.cochat.models

data class Configuration(
    var chatBackground: String = "",
    @field:JvmField
    var isPushNotificationEnabled: Boolean = true,
)