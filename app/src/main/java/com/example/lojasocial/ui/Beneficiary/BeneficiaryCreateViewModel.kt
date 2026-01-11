package com.example.lojasocial.ui.beneficiary

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.Beneficiary
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

data class BeneficiaryCreateState(
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

class BeneficiaryCreateViewModel : ViewModel() {

    var uiState = mutableStateOf(BeneficiaryCreateState())
        private set

    private val db = Firebase.firestore

    fun setNumeroAluno(v: String) { uiState.value = uiState.value.copy(numeroAluno = v) }
    fun setNome(v: String) { uiState.value = uiState.value.copy(nome = v) }
    fun setNif(v: String) { uiState.value = uiState.value.copy(nif = v) }
    fun setDataNascimento(v: String) { uiState.value = uiState.value.copy(dataNascimento = v) }
    fun setEmail(v: String) { uiState.value = uiState.value.copy(email = v) }
    fun setCurso(v: String) { uiState.value = uiState.value.copy(curso = v) }
    fun setAno(v: String) { uiState.value = uiState.value.copy(ano = v) }

    private fun validate(): Boolean {
        if (uiState.value.nome.isNullOrBlank()) {
            uiState.value = uiState.value.copy(error = "Nome é obrigatório", isLoading = false)
            return false
        }
        if (uiState.value.nif.isNullOrBlank()) {
            uiState.value = uiState.value.copy(error = "NIF é obrigatório", isLoading = false)
            return false
        }
        if (uiState.value.nif!!.length != 9 || uiState.value.nif!!.any { !it.isDigit() }) {
            uiState.value = uiState.value.copy(error = "NIF deve ter 9 dígitos", isLoading = false)
            return false
        }
        if (uiState.value.email.isNullOrBlank()) {
            uiState.value = uiState.value.copy(error = "Email é obrigatório", isLoading = false)
            return false
        }
        if (!uiState.value.email!!.contains("@")) {
            uiState.value = uiState.value.copy(error = "Email inválido", isLoading = false)
            return false
        }
        return true
    }

    fun create(onSuccess: () -> Unit) {
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        if (!validate()) return

        val data = Beneficiary(
            numeroAluno = uiState.value.numeroAluno,
            nome = uiState.value.nome,
            nif = uiState.value.nif,
            dataNascimento = uiState.value.dataNascimento,
            email = uiState.value.email,
            curso = uiState.value.curso,
            ano = uiState.value.ano
        )

        db.collection("beneficiaries")
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