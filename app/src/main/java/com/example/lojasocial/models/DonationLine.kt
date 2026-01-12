package com.example.lojasocial.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class DonationLine(
    var docId: String? = null,
    var itemId: String? = null,
    var itemName: String? = null,
    var quantity: Int = 0,
    var expiryDate: Timestamp? = null
)
