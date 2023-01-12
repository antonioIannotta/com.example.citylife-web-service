package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class AccessInformation(val userEmail: String, val userPassword: String)
