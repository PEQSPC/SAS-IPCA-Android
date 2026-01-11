package com.example.lojasocial.ui.agendas

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.Agenda
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class AgendasListViewModel : ViewModel() {

    private val db = Firebase.firestore
    private var listener: ListenerRegistration? = null

    var uiState = mutableStateOf(AgendasListState())
        private set

    fun setSearch(v: String) { uiState.value = uiState.value.copy(search = v) }
    fun setTypeFilter(v: String) { uiState.value = uiState.value.copy(typeFilter = v) }
    fun setStatusFilter(v: String) { uiState.value = uiState.value.copy(statusFilter = v) }
    fun setDateFrom(v: String) { uiState.value = uiState.value.copy(dateFrom = v) }
    fun setDateTo(v: String) { uiState.value = uiState.value.copy(dateTo = v) }

    fun fetch() {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        listener?.remove()
        listener = db.collection("agendas")
            .addSnapshotListener { result, error ->
                if (error != null) {
                    uiState.value = uiState.value.copy(isLoading = false, error = error.message)
                    return@addSnapshotListener
                }

                val list = mutableListOf<Agenda>()
                for (doc in result?.documents ?: emptyList()) {
                    val a = doc.toObject(Agenda::class.java)
                    if (a != null) {
                        a.docId = doc.id
                        list.add(a)
                    }
                }

                uiState.value = uiState.value.copy(
                    items = list.sortedBy { it.date ?: "" },
                    isLoading = false,
                    error = null
                )
            }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}