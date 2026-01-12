package com.example.lojasocial.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Delivery(
    var docId: String? = null,
    var beneficiaryId: String? = null,
    var beneficiaryName: String? = null,
    var status: String? = null,      // "SCHEDULED" | "DELIVERED"
    var scheduledAt: Timestamp? = null
)
