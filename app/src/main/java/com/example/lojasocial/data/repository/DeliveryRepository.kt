package com.example.lojasocial.data.repository

import android.util.Log
import com.example.lojasocial.models.Delivery
import com.example.lojasocial.models.DeliveryLine
import com.example.lojasocial.models.ResultWrapper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DeliveryRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    fun getDeliveries(): Flow<ResultWrapper<List<Delivery>>> = flow {
        try {
            Log.d("DeliveryRepository", "getDeliveries() iniciado")
            emit(ResultWrapper.Loading())

            val snapshot = db.collection("deliveries")
                .get()
                .await()

            val deliveries = snapshot.documents.mapNotNull { doc ->
                val delivery = doc.toObject(Delivery::class.java)
                delivery?.docId = doc.id
                delivery
            }

            Log.d("DeliveryRepository", "Deliveries obtidas: ${deliveries.size}")
            emit(ResultWrapper.Success(deliveries))

        } catch (e: Exception) {
            Log.e("DeliveryRepository", "Erro ao obter deliveries: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun getDeliveriesByBeneficiary(beneficiaryId: String): Flow<ResultWrapper<List<Delivery>>> = flow {
        try {
            Log.d("DeliveryRepository", "getDeliveriesByBeneficiary() - beneficiaryId: $beneficiaryId")
            emit(ResultWrapper.Loading())

            val snapshot = db.collection("deliveries")
                .whereEqualTo("beneficiaryId", beneficiaryId)
                .get()
                .await()

            val deliveries = snapshot.documents.mapNotNull { doc ->
                val delivery = doc.toObject(Delivery::class.java)
                delivery?.docId = doc.id
                delivery
            }

            Log.d("DeliveryRepository", "Deliveries obtidas: ${deliveries.size}")
            emit(ResultWrapper.Success(deliveries))

        } catch (e: Exception) {
            Log.e("DeliveryRepository", "Erro ao obter deliveries: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun createDelivery(delivery: Delivery): Flow<ResultWrapper<String>> = flow {
        try {
            Log.d("DeliveryRepository", "createDelivery() iniciado - beneficiary: ${delivery.beneficiaryName}")
            emit(ResultWrapper.Loading())

            val docRef = db.collection("deliveries").add(delivery).await()

            Log.d("DeliveryRepository", "Delivery criada com ID: ${docRef.id}")
            emit(ResultWrapper.Success(docRef.id))

        } catch (e: Exception) {
            Log.e("DeliveryRepository", "Erro ao criar delivery: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun updateDeliveryStatus(deliveryId: String, status: String): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("DeliveryRepository", "updateDeliveryStatus() - deliveryId: $deliveryId, status: $status")
            emit(ResultWrapper.Loading())

            db.collection("deliveries")
                .document(deliveryId)
                .update("status", status)
                .await()

            Log.d("DeliveryRepository", "Delivery status atualizado com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("DeliveryRepository", "Erro ao atualizar delivery status: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun addDeliveryLine(deliveryId: String, line: DeliveryLine): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("DeliveryRepository", "addDeliveryLine() - deliveryId: $deliveryId")
            emit(ResultWrapper.Loading())

            db.collection("deliveries")
                .document(deliveryId)
                .collection("lines")
                .add(line)
                .await()

            Log.d("DeliveryRepository", "DeliveryLine adicionada com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("DeliveryRepository", "Erro ao adicionar deliveryLine: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun getDeliveryLines(deliveryId: String): Flow<ResultWrapper<List<DeliveryLine>>> = flow {
        try {
            Log.d("DeliveryRepository", "getDeliveryLines() - deliveryId: $deliveryId")
            emit(ResultWrapper.Loading())

            val snapshot = db.collection("deliveries")
                .document(deliveryId)
                .collection("lines")
                .get()
                .await()

            val lines = snapshot.documents.mapNotNull { doc ->
                val line = doc.toObject(DeliveryLine::class.java)
                line?.docId = doc.id
                line
            }

            Log.d("DeliveryRepository", "DeliveryLines obtidas: ${lines.size}")
            emit(ResultWrapper.Success(lines))

        } catch (e: Exception) {
            Log.e("DeliveryRepository", "Erro ao obter deliveryLines: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)
}
