package com.example.models

import kotlinx.serialization.Serializable

/**
 * Data class di supporto utilizzata per incapsulare i dati che vengono passati dall'utente al momento dell'accesso
 * al sistema.
 */
@Serializable
data class AccessInformation(val userEmail: String, val userPassword: String)
