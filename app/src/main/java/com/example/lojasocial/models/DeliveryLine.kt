package com.example.lojasocial.models

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class DeliveryLine(
    var docId: String? = null,
    var itemId: String? = null,
    var lotId: String? = null,
    var quantity: Int = 0
)
