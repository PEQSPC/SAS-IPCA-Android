package com.example.lojasocial.ui.beneficiary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.Beneficiary
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BeneficiariesListViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private var listener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(BeneficiariesListState())
    val uiState: StateFlow<BeneficiariesListState> = _uiState.asStateFlow()

    fun setSearch(text: String) {
        _uiState.value = _uiState.value.copy(search = text)
    }

    fun setStatusFilter(status: String?) {
        _uiState.value = _uiState.value.copy(statusFilter = status)
    }

    fun fetch() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        listener?.remove()
        listener = db.collection("beneficiaries")
            .addSnapshotListener { result, error ->
                if (error != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                    return@addSnapshotListener
                }

                val list = mutableListOf<Beneficiary>()
                for (doc in result?.documents ?: emptyList()) {
                    val b = doc.toObject(Beneficiary::class.java)
                    if (b != null) {
                        b.docId = doc.id
                        list.add(b)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    items = list.sortedBy { it.nome ?: "" },
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