package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var name: String,
    var surname: String,
    var username: String,
    var email: String,
    val password: String,
    val distance: String,
    val location: String
)
