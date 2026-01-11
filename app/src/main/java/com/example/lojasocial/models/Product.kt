package com.example.lojasocial.models

data class Products(
    var docId: String? = null,
    var sku: String? = null,
    var name: String? = null,
    var familia: String? = null,
    var unidade: String? = null,
    var localizacao: String? = null,
    var stockMinimo: Int? = null,
    var stockAtual: Int? = null,
    var eans: String? = null,
    var notas: String? = null
)