package com.example.lojasocial.models

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Donor(
    var docId: String? = null,
    var type: String? = null,        // "COMPANY" | "PRIVATE"
    var name: String? = null,
    var email: String? = null,
    var nif: String? = null
)
