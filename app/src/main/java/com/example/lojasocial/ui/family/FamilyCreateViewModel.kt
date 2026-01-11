package com.example.lojasocial.ui.family

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.Family
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FamilyCreateState(
    val name: String? = null,
    val notes: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class FamilyCreateViewModel : ViewModel() {

    private val db = Firebase.firestore

    var uiState = mutableStateOf(FamilyCreateState())
        private set

    fun setName(v: String) { uiState.value = uiState.value.copy(name = v) }
    fun setNotes(v: String) { uiState.value = uiState.value.copy(notes = v) }

    private fun nowStr(): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    fun create(onSuccess: () -> Unit) {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        if (uiState.value.name.isNullOrBlank()) {
            uiState.value = uiState.value.copy(isLoading = false, error = "Nome é obrigatório")
            return
        }

        val data = Family(
            name = uiState.value.name?.trim(),
            notes = uiState.value.notes?.trim(),
            createdAt = nowStr(),
            updatedAt = nowStr()
        )

        db.collection("families")
            .add(data)
            .addOnSuccessListener {
                uiState.value = uiState.value.copy(isLoading = false, error = null)
                onSuccess()
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.message)
            }
    }
}