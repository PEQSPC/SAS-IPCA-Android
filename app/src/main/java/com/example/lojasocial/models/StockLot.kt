package com.example.lojasocial.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class StockLot(
    var docId: String? = null,
    var lot: String? = null,         // Lote ID
    var quantity: Int = 0,           // Quantidade inicial
    var remainingQty: Int = 0,       // Quantidade restante
    @get:PropertyName("expiryDate")
    @set:PropertyName("expiryDate")
    var expiryDate: Timestamp? = null,
    var donorId: String? = null
)
