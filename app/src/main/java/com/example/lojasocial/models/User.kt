package com.example.lojasocial.models

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class User(

    var docId: String? = null,

    var name: String? = null,

    var email: String? = null,

    @get:PropertyName("typeUser")
    @set:PropertyName("typeUser")
    var userType: String? = null
)