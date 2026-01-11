package com.example.lojasocial.ui.agendas

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.Agenda
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

data class AgendasCreateState(
    val entity: String? = null,
    val date: String? = null,          // yyyy-MM-dd
    val isLoading: Boolean = false,
    val error: String? = null
)

class AgendasCreateViewModel : ViewModel() {

    private val db = Firebase.firestore

    var uiState = mutableStateOf(AgendasCreateState())
        private set

    fun setEntity(value: String) {
        uiState.value = uiState.value.copy(entity = value, error = null)
    }

    fun setDate(value: String) {
        uiState.value = uiState.value.copy(date = value, error = null)
    }

    fun create(onSuccess: () -> Unit) {
        val name = uiState.value.entity?.trim().orEmpty()
        val date = uiState.value.date?.trim().orEmpty()

        if (name.isBlank()) {
            uiState.value = uiState.value.copy(error = "O nome é obrigatório.")
            return
        }
        if (date.isBlank()) {
            uiState.value = uiState.value.copy(error = "A data é obrigatória.")
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, error = null)

        val agenda = Agenda(
            entity = name,
            date = date
        )

        db.collection("agendas")
            .add(agenda)
            .addOnSuccessListener {
                uiState.value = uiState.value.copy(isLoading = false, error = null)
                onSuccess()
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.message)
            }
    }
}