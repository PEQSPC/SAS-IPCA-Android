package com.example.lojasocial.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Beneficiary(
    var docId: String? = null,
    var numeroAluno: String? = null,     // studentNumber
    var nome: String? = null,            // name
    var nif: String? = null,              // nif
    var dataNascimento: String? = null,   // birthDate
    var email: String? = null,
    var phone: String? = null,           // NOVO campo
    var curso: String? = null,           // course
    var ano: String? = null,             // curricularYear
    var status: String? = "ACTIVE",      // "ACTIVE", "PENDING", "INACTIVE"
    var lastDeliveryDate: Timestamp? = null,
    var createdAt: Timestamp? = null,
    var updatedAt: Timestamp? = null
)