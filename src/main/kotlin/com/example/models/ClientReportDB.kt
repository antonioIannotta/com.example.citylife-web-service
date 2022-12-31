package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ClientReportDB(
    var type: String,
    var location: String,
    var localDateTime: String,
    var text: String,
    var username: String
)
