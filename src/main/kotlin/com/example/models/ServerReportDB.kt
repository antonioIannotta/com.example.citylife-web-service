package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ServerReportDB(
    var type: String,
    var location: String,
    var localDateTime: String,
    var text: String,
    var listOfUsername: String
)
