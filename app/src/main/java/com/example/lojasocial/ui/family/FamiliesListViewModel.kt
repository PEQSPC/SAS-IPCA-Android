package com.example.lojasocial.ui.family

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.Family
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FamiliesListState(
    val items: List<Family> = emptyList(),
    val search: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FamiliesListViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private var listener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(FamiliesListState())
    val uiState: StateFlow<FamiliesListState> = _uiState.asStateFlow()

    fun setSearch(text: String) {
        _uiState.value = _uiState.value.copy(search = text)
    }

    fun fetch() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        listener?.remove()
        listener = db.collection("families")
            .addSnapshotListener { result, error ->
                if (error != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                    return@addSnapshotListener
                }

                val list = result?.documents?.mapNotNull { doc ->
                    doc.toObject(Family::class.java)?.apply { docId = doc.id }
                } ?: emptyList()

                _uiState.value = _uiState.value.copy(
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