package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ServerReport(
    var type: String,
    var location: String,
    var localDateTime: String,
    var text: String,
    var listOfUsername: String
)
