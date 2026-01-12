package com.example.lojasocial.data.repository

import android.util.Log
import com.example.lojasocial.models.Beneficiary
import com.example.lojasocial.models.ResultWrapper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BeneficiaryRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    fun getBeneficiaries(): Flow<ResultWrapper<List<Beneficiary>>> = flow {
        try {
            Log.d("BeneficiaryRepository", "getBeneficiaries() iniciado")
            emit(ResultWrapper.Loading())

            val snapshot = db.collection("beneficiaries")
                .get()
                .await()

            val beneficiaries = snapshot.documents.mapNotNull { doc ->
                val beneficiary = doc.toObject(Beneficiary::class.java)
                beneficiary?.docId = doc.id
                beneficiary
            }

            Log.d("BeneficiaryRepository", "Beneficiaries obtidos: ${beneficiaries.size}")
            emit(ResultWrapper.Success(beneficiaries))

        } catch (e: Exception) {
            Log.e("BeneficiaryRepository", "Erro ao obter beneficiaries: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun getBeneficiaryById(id: String): Flow<ResultWrapper<Beneficiary?>> = flow {
        try {
            Log.d("BeneficiaryRepository", "getBeneficiaryById() iniciado - id: $id")
            emit(ResultWrapper.Loading())

            val document = db.collection("beneficiaries")
                .document(id)
                .get()
                .await()

            val beneficiary = document.toObject(Beneficiary::class.java)
            beneficiary?.docId = document.id

            Log.d("BeneficiaryRepository", "Beneficiary obtido: ${beneficiary?.nome}")
            emit(ResultWrapper.Success(beneficiary))

        } catch (e: Exception) {
            Log.e("BeneficiaryRepository", "Erro ao obter beneficiary: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun createBeneficiary(beneficiary: Beneficiary): Flow<ResultWrapper<String>> = flow {
        try {
            Log.d("BeneficiaryRepository", "createBeneficiary() iniciado - name: ${beneficiary.nome}")
            emit(ResultWrapper.Loading())

            val docRef = db.collection("beneficiaries").add(beneficiary).await()

            Log.d("BeneficiaryRepository", "Beneficiary criado com ID: ${docRef.id}")
            emit(ResultWrapper.Success(docRef.id))

        } catch (e: Exception) {
            Log.e("BeneficiaryRepository", "Erro ao criar beneficiary: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun updateBeneficiary(beneficiary: Beneficiary): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("BeneficiaryRepository", "updateBeneficiary() iniciado - id: ${beneficiary.docId}")
            emit(ResultWrapper.Loading())

            val docId = beneficiary.docId ?: throw Exception("Beneficiary ID is null")

            db.collection("beneficiaries")
                .document(docId)
                .set(beneficiary)
                .await()

            Log.d("BeneficiaryRepository", "Beneficiary atualizado com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("BeneficiaryRepository", "Erro ao atualizar beneficiary: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteBeneficiary(id: String): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("BeneficiaryRepository", "deleteBeneficiary() iniciado - id: $id")
            emit(ResultWrapper.Loading())

            db.collection("beneficiaries")
                .document(id)
                .delete()
                .await()

            Log.d("BeneficiaryRepository", "Beneficiary eliminado com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("BeneficiaryRepository", "Erro ao eliminar beneficiary: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)
}
