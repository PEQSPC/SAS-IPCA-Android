package com.example.lojasocial.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Donation(
    var docId: String? = null,
    var donorId: String? = null,
    var donorName: String? = null,
    var date: Timestamp? = null,
    var notes: String? = null
)
