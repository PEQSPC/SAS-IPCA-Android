package com.example.lojasocial.data.repository

import android.util.Log
import com.example.lojasocial.models.ResultWrapper
import com.example.lojasocial.models.StockLot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StockLotRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    fun getStockLots(itemId: String): Flow<ResultWrapper<List<StockLot>>> = flow {
        try {
            Log.d("StockLotRepository", "getStockLots() iniciado - itemId: $itemId")
            emit(ResultWrapper.Loading())

            val snapshot = db.collection("items")
                .document(itemId)
                .collection("stockLots")
                .get()
                .await()

            val stockLots = snapshot.documents.mapNotNull { doc ->
                val lot = doc.toObject(StockLot::class.java)
                lot?.docId = doc.id
                lot
            }

            Log.d("StockLotRepository", "StockLots obtidos: ${stockLots.size}")
            emit(ResultWrapper.Success(stockLots))

        } catch (e: Exception) {
            Log.e("StockLotRepository", "Erro ao obter stockLots: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun getStockLotById(itemId: String, lotId: String): Flow<ResultWrapper<StockLot?>> = flow {
        try {
            Log.d("StockLotRepository", "getStockLotById() - itemId: $itemId, lotId: $lotId")
            emit(ResultWrapper.Loading())

            val document = db.collection("items")
                .document(itemId)
                .collection("stockLots")
                .document(lotId)
                .get()
                .await()

            val lot = document.toObject(StockLot::class.java)
            lot?.docId = document.id

            Log.d("StockLotRepository", "StockLot obtido: ${lot?.lot}")
            emit(ResultWrapper.Success(lot))

        } catch (e: Exception) {
            Log.e("StockLotRepository", "Erro ao obter stockLot: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun createStockLot(itemId: String, lot: StockLot): Flow<ResultWrapper<String>> = flow {
        try {
            Log.d("StockLotRepository", "createStockLot() - itemId: $itemId, lot: ${lot.lot}")
            emit(ResultWrapper.Loading())

            val docRef = db.collection("items")
                .document(itemId)
                .collection("stockLots")
                .add(lot)
                .await()

            Log.d("StockLotRepository", "StockLot criado com ID: ${docRef.id}")
            emit(ResultWrapper.Success(docRef.id))

        } catch (e: Exception) {
            Log.e("StockLotRepository", "Erro ao criar stockLot: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun updateStockLot(itemId: String, lot: StockLot): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("StockLotRepository", "updateStockLot() - itemId: $itemId, lotId: ${lot.docId}")
            emit(ResultWrapper.Loading())

            val lotId = lot.docId ?: throw Exception("StockLot ID is null")

            db.collection("items")
                .document(itemId)
                .collection("stockLots")
                .document(lotId)
                .set(lot)
                .await()

            Log.d("StockLotRepository", "StockLot atualizado com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("StockLotRepository", "Erro ao atualizar stockLot: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun consumeStock(itemId: String, lotId: String, qty: Int): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("StockLotRepository", "consumeStock() - itemId: $itemId, lotId: $lotId, qty: $qty")
            emit(ResultWrapper.Loading())

            val docRef = db.collection("items")
                .document(itemId)
                .collection("stockLots")
                .document(lotId)

            val document = docRef.get().await()
            val lot = document.toObject(StockLot::class.java)

            if (lot == null) {
                throw Exception("StockLot não encontrado")
            }

            if (lot.remainingQty < qty) {
                throw Exception("Stock insuficiente (disponível: ${lot.remainingQty}, pedido: $qty)")
            }

            val newRemainingQty = lot.remainingQty - qty

            docRef.update("remainingQty", newRemainingQty).await()

            Log.d("StockLotRepository", "Stock consumido com sucesso - remainingQty: $newRemainingQty")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("StockLotRepository", "Erro ao consumir stock: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)
}
