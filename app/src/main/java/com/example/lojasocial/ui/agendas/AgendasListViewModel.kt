package com.example.lojasocial.ui.agendas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.Agenda
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgendasListViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private var listener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(AgendasListState())
    val uiState: StateFlow<AgendasListState> = _uiState.asStateFlow()

    fun setSearch(v: String) { _uiState.value = _uiState.value.copy(search = v) }
    fun setTypeFilter(v: String) { _uiState.value = _uiState.value.copy(typeFilter = v) }
    fun setStatusFilter(v: String) { _uiState.value = _uiState.value.copy(statusFilter = v) }
    fun setDateFrom(v: String) { _uiState.value = _uiState.value.copy(dateFrom = v) }
    fun setDateTo(v: String) { _uiState.value = _uiState.value.copy(dateTo = v) }

    fun fetch() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        listener?.remove()
        listener = db.collection("agendas")
            .addSnapshotListener { result, error ->
                if (error != null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
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

                _uiState.value = _uiState.value.copy(
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