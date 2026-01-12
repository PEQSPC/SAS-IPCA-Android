package com.example.lojasocial.data.repository

import android.util.Log
import com.example.lojasocial.models.Family
import com.example.lojasocial.models.ResultWrapper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FamilyRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    fun getFamilies(): Flow<ResultWrapper<List<Family>>> = flow {
        try {
            Log.d("FamilyRepository", "getFamilies() iniciado")
            emit(ResultWrapper.Loading())

            val snapshot = db.collection("families")
                .get()
                .await()

            val families = snapshot.documents.mapNotNull { doc ->
                val family = doc.toObject(Family::class.java)
                family?.docId = doc.id
                family
            }

            Log.d("FamilyRepository", "Families obtidas: ${families.size}")
            emit(ResultWrapper.Success(families))

        } catch (e: Exception) {
            Log.e("FamilyRepository", "Erro ao obter families: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun getFamilyById(id: String): Flow<ResultWrapper<Family?>> = flow {
        try {
            Log.d("FamilyRepository", "getFamilyById() iniciado - id: $id")
            emit(ResultWrapper.Loading())

            val document = db.collection("families")
                .document(id)
                .get()
                .await()

            val family = document.toObject(Family::class.java)
            family?.docId = document.id

            Log.d("FamilyRepository", "Family obtida: ${family?.name}")
            emit(ResultWrapper.Success(family))

        } catch (e: Exception) {
            Log.e("FamilyRepository", "Erro ao obter family: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun createFamily(family: Family): Flow<ResultWrapper<String>> = flow {
        try {
            Log.d("FamilyRepository", "createFamily() iniciado - name: ${family.name}")
            emit(ResultWrapper.Loading())

            val docRef = db.collection("families").add(family).await()

            Log.d("FamilyRepository", "Family criada com ID: ${docRef.id}")
            emit(ResultWrapper.Success(docRef.id))

        } catch (e: Exception) {
            Log.e("FamilyRepository", "Erro ao criar family: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun updateFamily(family: Family): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("FamilyRepository", "updateFamily() iniciado - id: ${family.docId}")
            emit(ResultWrapper.Loading())

            val docId = family.docId ?: throw Exception("Family ID is null")

            db.collection("families")
                .document(docId)
                .set(family)
                .await()

            Log.d("FamilyRepository", "Family atualizada com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("FamilyRepository", "Erro ao atualizar family: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteFamily(id: String): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("FamilyRepository", "deleteFamily() iniciado - id: $id")
            emit(ResultWrapper.Loading())

            db.collection("families")
                .document(id)
                .delete()
                .await()

            Log.d("FamilyRepository", "Family eliminada com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("FamilyRepository", "Erro ao eliminar family: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)
}
