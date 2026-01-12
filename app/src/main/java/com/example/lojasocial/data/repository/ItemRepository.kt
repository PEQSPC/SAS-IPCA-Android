package com.example.lojasocial.data.repository

import android.util.Log
import com.example.lojasocial.models.Item
import com.example.lojasocial.models.ResultWrapper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ItemRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    fun getItems(): Flow<ResultWrapper<List<Item>>> = flow {
        try {
            Log.d("ItemRepository", "getItems() iniciado")
            emit(ResultWrapper.Loading())

            val snapshot = db.collection("items")
                .get()
                .await()

            val items = snapshot.documents.mapNotNull { doc ->
                val item = doc.toObject(Item::class.java)
                item?.docId = doc.id
                item
            }

            Log.d("ItemRepository", "Items obtidos: ${items.size}")
            emit(ResultWrapper.Success(items))

        } catch (e: Exception) {
            Log.e("ItemRepository", "Erro ao obter items: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun getItemById(id: String): Flow<ResultWrapper<Item?>> = flow {
        try {
            Log.d("ItemRepository", "getItemById() iniciado - id: $id")
            emit(ResultWrapper.Loading())

            val document = db.collection("items")
                .document(id)
                .get()
                .await()

            val item = document.toObject(Item::class.java)
            item?.docId = document.id

            Log.d("ItemRepository", "Item obtido: ${item?.name}")
            emit(ResultWrapper.Success(item))

        } catch (e: Exception) {
            Log.e("ItemRepository", "Erro ao obter item: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun createItem(item: Item): Flow<ResultWrapper<String>> = flow {
        try {
            Log.d("ItemRepository", "createItem() iniciado - name: ${item.name}")
            emit(ResultWrapper.Loading())

            val docRef = db.collection("items").add(item).await()

            Log.d("ItemRepository", "Item criado com ID: ${docRef.id}")
            emit(ResultWrapper.Success(docRef.id))

        } catch (e: Exception) {
            Log.e("ItemRepository", "Erro ao criar item: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun updateItem(item: Item): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("ItemRepository", "updateItem() iniciado - id: ${item.docId}")
            emit(ResultWrapper.Loading())

            val docId = item.docId ?: throw Exception("Item ID is null")

            db.collection("items")
                .document(docId)
                .set(item)
                .await()

            Log.d("ItemRepository", "Item atualizado com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("ItemRepository", "Erro ao atualizar item: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteItem(id: String): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("ItemRepository", "deleteItem() iniciado - id: $id")
            emit(ResultWrapper.Loading())

            db.collection("items")
                .document(id)
                .delete()
                .await()

            Log.d("ItemRepository", "Item eliminado com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("ItemRepository", "Erro ao eliminar item: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)
}
