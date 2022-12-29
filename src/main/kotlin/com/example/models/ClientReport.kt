package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ClientReport(
    var type: String,
    var location: String,
    var localDateTime: String,
    var text: String,
    var username: String
)
