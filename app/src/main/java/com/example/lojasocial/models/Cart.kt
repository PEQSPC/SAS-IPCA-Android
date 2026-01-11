package com.example.lojasocial.models

data class Cart (
    var docId  : String? = null,
    var name   : String? = null,
    var owners   : List<String>? = null,
)