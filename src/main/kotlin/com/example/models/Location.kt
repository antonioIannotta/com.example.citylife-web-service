package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    var username: String,
    var distance: String,
    var location: String
)