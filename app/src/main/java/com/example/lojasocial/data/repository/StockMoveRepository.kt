package com.example.lojasocial.data.repository

import android.util.Log
import com.example.lojasocial.models.ResultWrapper
import com.example.lojasocial.models.StockMove
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StockMoveRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    fun getStockMoves(itemId: String? = null): Flow<ResultWrapper<List<StockMove>>> = flow {
        try {
            Log.d("StockMoveRepository", "getStockMoves() iniciado - itemId: $itemId")
            emit(ResultWrapper.Loading())

            val query = if (itemId != null) {
                db.collection("stockMoves").whereEqualTo("itemId", itemId)
            } else {
                db.collection("stockMoves")
            }

            val snapshot = query.get().await()

            val stockMoves = snapshot.documents.mapNotNull { doc ->
                val move = doc.toObject(StockMove::class.java)
                move?.docId = doc.id
                move
            }

            Log.d("StockMoveRepository", "StockMoves obtidos: ${stockMoves.size}")
            emit(ResultWrapper.Success(stockMoves))

        } catch (e: Exception) {
            Log.e("StockMoveRepository", "Erro ao obter stockMoves: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun createStockMove(move: StockMove): Flow<ResultWrapper<String>> = flow {
        try {
            Log.d("StockMoveRepository", "createStockMove() iniciado - type: ${move.type}, qty: ${move.quantity}")
            emit(ResultWrapper.Loading())

            val docRef = db.collection("stockMoves").add(move).await()

            Log.d("StockMoveRepository", "StockMove criado com ID: ${docRef.id}")
            emit(ResultWrapper.Success(docRef.id))

        } catch (e: Exception) {
            Log.e("StockMoveRepository", "Erro ao criar stockMove: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)
}
