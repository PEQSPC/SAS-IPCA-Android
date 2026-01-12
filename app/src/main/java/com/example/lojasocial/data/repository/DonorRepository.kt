package com.example.lojasocial.data.repository

import android.util.Log
import com.example.lojasocial.models.Donor
import com.example.lojasocial.models.ResultWrapper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DonorRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    fun getDonors(): Flow<ResultWrapper<List<Donor>>> = flow {
        try {
            Log.d("DonorRepository", "getDonors() iniciado")
            emit(ResultWrapper.Loading())

            val snapshot = db.collection("donors")
                .get()
                .await()

            val donors = snapshot.documents.mapNotNull { doc ->
                val donor = doc.toObject(Donor::class.java)
                donor?.docId = doc.id
                donor
            }

            Log.d("DonorRepository", "Donors obtidos: ${donors.size}")
            emit(ResultWrapper.Success(donors))

        } catch (e: Exception) {
            Log.e("DonorRepository", "Erro ao obter donors: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun getDonorById(id: String): Flow<ResultWrapper<Donor?>> = flow {
        try {
            Log.d("DonorRepository", "getDonorById() iniciado - id: $id")
            emit(ResultWrapper.Loading())

            val document = db.collection("donors")
                .document(id)
                .get()
                .await()

            val donor = document.toObject(Donor::class.java)
            donor?.docId = document.id

            Log.d("DonorRepository", "Donor obtido: ${donor?.name}")
            emit(ResultWrapper.Success(donor))

        } catch (e: Exception) {
            Log.e("DonorRepository", "Erro ao obter donor: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun createDonor(donor: Donor): Flow<ResultWrapper<String>> = flow {
        try {
            Log.d("DonorRepository", "createDonor() iniciado - name: ${donor.name}")
            emit(ResultWrapper.Loading())

            val docRef = db.collection("donors").add(donor).await()

            Log.d("DonorRepository", "Donor criado com ID: ${docRef.id}")
            emit(ResultWrapper.Success(docRef.id))

        } catch (e: Exception) {
            Log.e("DonorRepository", "Erro ao criar donor: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun updateDonor(donor: Donor): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("DonorRepository", "updateDonor() iniciado - id: ${donor.docId}")
            emit(ResultWrapper.Loading())

            val docId = donor.docId ?: throw Exception("Donor ID is null")

            db.collection("donors")
                .document(docId)
                .set(donor)
                .await()

            Log.d("DonorRepository", "Donor atualizado com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("DonorRepository", "Erro ao atualizar donor: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteDonor(id: String): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("DonorRepository", "deleteDonor() iniciado - id: $id")
            emit(ResultWrapper.Loading())

            db.collection("donors")
                .document(id)
                .delete()
                .await()

            Log.d("DonorRepository", "Donor eliminado com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("DonorRepository", "Erro ao eliminar donor: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)
}
