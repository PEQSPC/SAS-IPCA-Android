package com.example.lojasocial.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.let
import kotlin.to

class UserRepository @Inject constructor(
    private val db: FirebaseFirestore
)  {

     fun get(uid: String): Flow<ResultWrapper<User?>> = flow {
        try {
            emit(ResultWrapper.Loading())
            val document = db.collection("users")
                .document(uid)
                .get()
                .await()

            val user = document.toObject(User::class.java)
            user?.docId = document.id


            emit(ResultWrapper.Success(user))

        } catch (e: Exception) {
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)


}