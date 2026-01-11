package com.example.lojasocial.ui.agendas

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

data class AgendaDetailState(
    var date: String? = null,
    var timeWindow: String? = null,
    var type: String? = null,
    var status: String? = null,
    var entity: String? = null,
    var address: String? = null,
    var notes: String? = null,
    var error: String? = null,
    var isLoading: Boolean = false
)

class AgendaDetailViewModel : ViewModel() {

    private val db = Firebase.firestore
    private var docId: String? = null

    var uiState = mutableStateOf(AgendaDetailState())
        private set

    fun setDate(v: String) { uiState.value = uiState.value.copy(date = v) }
    fun setTimeWindow(v: String) { uiState.value = uiState.value.copy(timeWindow = v) }
    fun setType(v: String) { uiState.value = uiState.value.copy(type = v) }
    fun setStatus(v: String) { uiState.value = uiState.value.copy(status = v) }
    fun setEntity(v: String) { uiState.value = uiState.value.copy(entity = v) }
    fun setAddress(v: String) { uiState.value = uiState.value.copy(address = v) }
    fun setNotes(v: String) { uiState.value = uiState.value.copy(notes = v) }

    fun fetch(id: String) {
        docId = id
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        db.collection("agendas")
            .document(id)
            .get()
            .addOnSuccessListener { doc ->
                uiState.value = uiState.value.copy(
                    date = doc.getString("date"),
                    timeWindow = doc.getString("timeWindow"),
                    type = doc.getString("type"),
                    status = doc.getString("status"),
                    entity = doc.getString("entity"),
                    address = doc.getString("address"),
                    notes = doc.getString("notes"),
                    isLoading = false,
                    error = null
                )
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.message)
            }
    }

    fun save(onSuccess: () -> Unit) {
        val id = docId ?: return
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        val data = mapOf(
            "date" to uiState.value.date,
            "timeWindow" to uiState.value.timeWindow,
            "type" to uiState.value.type,
            "status" to uiState.value.status,
            "entity" to uiState.value.entity,
            "address" to uiState.value.address,
            "notes" to uiState.value.notes
        )

        db.collection("agendas")
            .document(id)
            .update(data)
            .addOnSuccessListener {
                uiState.value = uiState.value.copy(isLoading = false, error = null)
                onSuccess()
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.message)
            }
    }

    fun delete(onDeleted: () -> Unit) {
        val id = docId ?: return
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        db.collection("agendas")
            .document(id)
            .delete()
            .addOnSuccessListener {
                uiState.value = uiState.value.copy(isLoading = false, error = null)
                onDeleted()
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.message)
            }
    }
}