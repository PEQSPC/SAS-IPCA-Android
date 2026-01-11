package com.example.lojasocial.ui.beneficiary

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.Beneficiary
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class BeneficiariesListViewModel : ViewModel() {

    private val db = Firebase.firestore
    private var listener: ListenerRegistration? = null

    var uiState = mutableStateOf(BeneficiariesListState())
        private set

    fun setSearch(text: String) {
        uiState.value = uiState.value.copy(search = text)
    }

    fun fetch() {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        listener?.remove()
        listener = db.collection("beneficiaries")
            .addSnapshotListener { result, error ->
                if (error != null) {
                    uiState.value = uiState.value.copy(
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

                uiState.value = uiState.value.copy(
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