package com.example.lojasocial.models

data class Family(
    var docId: String? = null,
    var name: String? = null,
    var notes: String? = null,
    var createdAt: String? = null,   // ex: "08/11/2025"
    var updatedAt: String? = null    // ex: "08/11/2025"
)