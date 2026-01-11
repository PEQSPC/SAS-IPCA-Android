package com.example.lojasocial.ui.family

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.Family
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

data class FamiliesListState(
    val items: List<Family> = emptyList(),
    val search: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class FamiliesListViewModel : ViewModel() {

    private val db = Firebase.firestore
    private var listener: ListenerRegistration? = null

    var uiState = mutableStateOf(FamiliesListState())
        private set

    fun setSearch(text: String) {
        uiState.value = uiState.value.copy(search = text)
    }

    fun fetch() {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        listener?.remove()
        listener = db.collection("families")
            .addSnapshotListener { result, error ->
                if (error != null) {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                    return@addSnapshotListener
                }

                val list = result?.documents?.mapNotNull { doc ->
                    doc.toObject(Family::class.java)?.apply { docId = doc.id }
                } ?: emptyList()

                uiState.value = uiState.value.copy(
                    items = list.sortedBy { it.name ?: "" },
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