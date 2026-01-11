package com.example.lojasocial.ui.family

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FamilyDetailState(
    val name: String = "",
    val notes: String = "",
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class FamilyDetailViewModel : ViewModel() {

    private val db = Firebase.firestore
    private var docId: String? = null

    var uiState = mutableStateOf(FamilyDetailState())
        private set

    fun setName(v: String) { uiState.value = uiState.value.copy(name = v) }
    fun setNotes(v: String) { uiState.value = uiState.value.copy(notes = v) }

    private fun nowStr(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    fun fetch(id: String) {
        docId = id
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        db.collection("families")
            .document(id)
            .get()
            .addOnSuccessListener { doc ->
                uiState.value = uiState.value.copy(
                    name = doc.getString("name") ?: "",
                    notes = doc.getString("notes") ?: "",
                    createdAt = doc.getString("createdAt"),
                    updatedAt = doc.getString("updatedAt"),
                    isLoading = false,
                    error = null
                )
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.message)
            }
    }

    fun save(onSaved: () -> Unit) {
        val id = docId ?: return

        val name = uiState.value.name.trim()
        if (name.isBlank()) {
            uiState.value = uiState.value.copy(error = "Nome é obrigatório")
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, error = null)

        val updated = nowStr()
        val data = mapOf(
            "name" to name,
            "notes" to uiState.value.notes.trim().ifBlank { null },
            "updatedAt" to updated
        )

        db.collection("families")
            .document(id)
            .update(data)
            .addOnSuccessListener {
                uiState.value = uiState.value.copy(isLoading = false, updatedAt = updated)
                onSaved()
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.message)
            }
    }

    fun delete(onDeleted: () -> Unit) {
        val id = docId ?: return

        uiState.value = uiState.value.copy(isLoading = true, error = null)

        db.collection("families")
            .document(id)
            .delete()
            .addOnSuccessListener {
                uiState.value = uiState.value.copy(isLoading = false)
                onDeleted()
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.message)
            }
    }
}