package com.example.lojasocial.models

data class Agenda(
    var docId: String? = null,

    // filtros / tabela
    var date: String? = null,        // "yyyy-MM-dd" (ex: 2025-11-15)
    var timeWindow: String? = null,  // "09:00-12:00"
    var type: String? = null,        // "Entrega" | "Recolha" | "Doação" | ...
    var status: String? = null,      // "Planeado" | "Confirmado" | ...
    var entity: String? = null,      // ex: "Maria Alves" | "Mercado X"
    var address: String? = null,     // ex: "Rua do Instituto, 123"
    var notes: String? = null,

    // timestamps simples (strings para manter como tens no resto)
    var createdAt: String? = null,   // "dd/MM/yyyy" ou "yyyy-MM-dd"
    var updatedAt: String? = null
)