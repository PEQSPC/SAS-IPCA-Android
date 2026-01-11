package com.example.lojasocial.models

import android.util.Log
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
            Log.d("UserRepository", "get() iniciado - uid: $uid")
            emit(ResultWrapper.Loading())

            val document = db.collection("users")
                .document(uid)
                .get()
                .await()

            Log.d("UserRepository", "Documento obtido - exists: ${document.exists()}")
            Log.d("UserRepository", "Raw data: ${document.data}")
            Log.d("UserRepository", "typeUser field raw: ${document.get("typeUser")}")

            val user = document.toObject(User::class.java)
            user?.docId = document.id

            Log.d("UserRepository", "User mapeado - userType: '${user?.userType}', name: '${user?.name}', docId: '${user?.docId}'")

            emit(ResultWrapper.Success(user))

        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao obter user: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)


}