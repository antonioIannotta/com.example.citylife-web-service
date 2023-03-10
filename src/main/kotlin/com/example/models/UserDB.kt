package com.example.models

import kotlinx.serialization.Serializable


/**
 * Classe che modella un Utente
 */
@Serializable
data class UserDB(
    var name: String,
    var surname: String,
    var username: String,
    var email: String,
    val password: String,
    val distance: String,
    val location: String,
    val reportPreference: String
)
