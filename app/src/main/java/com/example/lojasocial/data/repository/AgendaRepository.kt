package com.example.lojasocial.data.repository

import android.util.Log
import com.example.lojasocial.models.Agenda
import com.example.lojasocial.models.ResultWrapper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AgendaRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    fun getAgendas(): Flow<ResultWrapper<List<Agenda>>> = flow {
        try {
            Log.d("AgendaRepository", "getAgendas() iniciado")
            emit(ResultWrapper.Loading())

            val snapshot = db.collection("agendas")
                .get()
                .await()

            val agendas = snapshot.documents.mapNotNull { doc ->
                val agenda = doc.toObject(Agenda::class.java)
                agenda?.docId = doc.id
                agenda
            }

            Log.d("AgendaRepository", "Agendas obtidas: ${agendas.size}")
            emit(ResultWrapper.Success(agendas))

        } catch (e: Exception) {
            Log.e("AgendaRepository", "Erro ao obter agendas: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun getAgendaById(id: String): Flow<ResultWrapper<Agenda?>> = flow {
        try {
            Log.d("AgendaRepository", "getAgendaById() iniciado - id: $id")
            emit(ResultWrapper.Loading())

            val document = db.collection("agendas")
                .document(id)
                .get()
                .await()

            val agenda = document.toObject(Agenda::class.java)
            agenda?.docId = document.id

            Log.d("AgendaRepository", "Agenda obtida: ${agenda?.entity}")
            emit(ResultWrapper.Success(agenda))

        } catch (e: Exception) {
            Log.e("AgendaRepository", "Erro ao obter agenda: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun createAgenda(agenda: Agenda): Flow<ResultWrapper<String>> = flow {
        try {
            Log.d("AgendaRepository", "createAgenda() iniciado - entity: ${agenda.entity}")
            emit(ResultWrapper.Loading())

            val docRef = db.collection("agendas").add(agenda).await()

            Log.d("AgendaRepository", "Agenda criada com ID: ${docRef.id}")
            emit(ResultWrapper.Success(docRef.id))

        } catch (e: Exception) {
            Log.e("AgendaRepository", "Erro ao criar agenda: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun updateAgenda(agenda: Agenda): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("AgendaRepository", "updateAgenda() iniciado - id: ${agenda.docId}")
            emit(ResultWrapper.Loading())

            val docId = agenda.docId ?: throw Exception("Agenda ID is null")

            db.collection("agendas")
                .document(docId)
                .set(agenda)
                .await()

            Log.d("AgendaRepository", "Agenda atualizada com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("AgendaRepository", "Erro ao atualizar agenda: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteAgenda(id: String): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("AgendaRepository", "deleteAgenda() iniciado - id: $id")
            emit(ResultWrapper.Loading())

            db.collection("agendas")
                .document(id)
                .delete()
                .await()

            Log.d("AgendaRepository", "Agenda eliminada com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("AgendaRepository", "Erro ao eliminar agenda: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)
}
