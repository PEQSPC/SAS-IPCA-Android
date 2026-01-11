package com.example.lojasocial.ui.beneficiary

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

data class BeneficiaryDetailState(
    var numeroAluno: String? = null,
    var nome: String? = null,
    var nif: String? = null,
    var dataNascimento: String? = null,
    var email: String? = null,
    var curso: String? = null,
    var ano: String? = null,
    var error: String? = null,
    var isLoading: Boolean = false
)

class BeneficiaryDetailViewModel : ViewModel() {

    var uiState = mutableStateOf(BeneficiaryDetailState())
        private set

    private val db = Firebase.firestore
    private var docId: String? = null

    fun setNumeroAluno(v: String) { uiState.value = uiState.value.copy(numeroAluno = v) }
    fun setNome(v: String) { uiState.value = uiState.value.copy(nome = v) }
    fun setNif(v: String) { uiState.value = uiState.value.copy(nif = v) }
    fun setDataNascimento(v: String) { uiState.value = uiState.value.copy(dataNascimento = v) }
    fun setEmail(v: String) { uiState.value = uiState.value.copy(email = v) }
    fun setCurso(v: String) { uiState.value = uiState.value.copy(curso = v) }
    fun setAno(v: String) { uiState.value = uiState.value.copy(ano = v) }

    fun fetch(id: String) {
        docId = id
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        db.collection("beneficiaries")
            .document(id)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = "Beneficiário não encontrado"
                    )
                    return@addOnSuccessListener
                }

                uiState.value = uiState.value.copy(
                    numeroAluno = doc.getString("numeroAluno"),
                    nome = doc.getString("nome"),
                    nif = doc.getString("nif"),
                    dataNascimento = doc.getString("dataNascimento"),
                    email = doc.getString("email"),
                    curso = doc.getString("curso"),
                    ano = doc.getString("ano"),
                    isLoading = false,
                    error = null
                )
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.message)
            }
    }

    fun save(onSuccess: () -> Unit = {}) {
        val id = docId ?: return

        uiState.value = uiState.value.copy(isLoading = true, error = null)

        val data = mapOf(
            "numeroAluno" to uiState.value.numeroAluno,
            "nome" to uiState.value.nome,
            "nif" to uiState.value.nif,
            "dataNascimento" to uiState.value.dataNascimento,
            "email" to uiState.value.email,
            "curso" to uiState.value.curso,
            "ano" to uiState.value.ano
        )

        db.collection("beneficiaries")
            .document(id)
            .update(data)
            .addOnSuccessListener {
                uiState.value = uiState.value.copy(isLoading = false, error = null)
                onSuccess() // ✅ navegação só depois de guardar
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.message)
            }
    }

    fun delete(onDeleted: () -> Unit) {
        val id = docId ?: return
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        db.collection("beneficiaries")
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