package com.example.lojasocial.models

data class Beneficiary(
    var docId: String? = null,
    var numeroAluno: String? = null,
    var nome: String? = null,
    var nif: String? = null,
    var dataNascimento: String? = null, // dd/MM/yyyy
    var email: String? = null,
    var curso: String? = null,
    var ano: String? = null
)