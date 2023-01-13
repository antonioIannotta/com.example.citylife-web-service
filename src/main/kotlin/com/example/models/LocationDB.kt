package com.example.models

import kotlinx.serialization.Serializable

/**
 * Classe che modella la Posizione di un utente con un certo username e la distanza di interesse
 */
@Serializable
data class LocationDB(
    var username: String,
    var distance: String,
    var location: String
)