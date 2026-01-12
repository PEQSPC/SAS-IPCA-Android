package com.example.lojasocial.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class StockMove(
    var docId: String? = null,
    var itemId: String? = null,
    var lotId: String? = null,
    var type: String? = null,        // "IN" | "OUT"
    var quantity: Int = 0,
    var createdAt: Timestamp? = null
)
