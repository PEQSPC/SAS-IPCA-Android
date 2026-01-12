package com.example.lojasocial.models

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Item(
    var docId: String? = null,
    var sku: String? = null,
    var name: String? = null,
    var familyId: String? = null,        // Referência ao Family.docId
    var unit: String? = null,            // "kg" | "unit"
    var minStock: Int? = null,           // stockMinimo
    var stockCurrent: Int? = null,       // stockAtual
    var localizacao: String? = null,     // Warehouse location (útil)
    var eans: String? = null,            // Barcode (útil)
    var notas: String? = null            // Notes (útil)
)
