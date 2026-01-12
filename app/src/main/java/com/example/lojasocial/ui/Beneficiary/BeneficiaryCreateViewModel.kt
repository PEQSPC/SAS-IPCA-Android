package com.example.lojasocial.ui.beneficiary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.Beneficiary
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BeneficiaryCreateState(
    var numeroAluno: String? = null,
    var nome: String? = null,
    var nif: String? = null,
    var dataNascimento: String? = null,
    var email: String? = null,
    var curso: String? = null,
    var ano: String? = null,
    var phone: String? = null,  // Novo campo
    var error: String? = null,
    var isLoading: Boolean = false
)

@HiltViewModel
class BeneficiaryCreateViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(BeneficiaryCreateState())
    val uiState: StateFlow<BeneficiaryCreateState> = _uiState.asStateFlow()

    fun setNumeroAluno(v: String) { _uiState.value = _uiState.value.copy(numeroAluno = v) }
    fun setNome(v: String) { _uiState.value = _uiState.value.copy(nome = v) }
    fun setNif(v: String) { _uiState.value = _uiState.value.copy(nif = v) }
    fun setDataNascimento(v: String) { _uiState.value = _uiState.value.copy(dataNascimento = v) }
    fun setEmail(v: String) { _uiState.value = _uiState.value.copy(email = v) }
    fun setCurso(v: String) { _uiState.value = _uiState.value.copy(curso = v) }
    fun setAno(v: String) { _uiState.value = _uiState.value.copy(ano = v) }
    fun setPhone(v: String) { _uiState.value = _uiState.value.copy(phone = v) }

    private fun validate(): Boolean {
        if (_uiState.value.nome.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "Nome é obrigatório", isLoading = false)
            return false
        }
        if (_uiState.value.email.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "Email é obrigatório", isLoading = false)
            return false
        }
        if (!_uiState.value.email!!.contains("@")) {
            _uiState.value = _uiState.value.copy(error = "Email inválido", isLoading = false)
            return false
        }
        return true
    }

    fun create(onSuccess: () -> Unit) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        if (!validate()) return

        val data = Beneficiary(
            numeroAluno = _uiState.value.numeroAluno,
            nome = _uiState.value.nome,
            email = _uiState.value.email,
            phone = _uiState.value.phone,
            curso = _uiState.value.curso,
            ano = _uiState.value.ano
        )

        db.collection("beneficiaries")
            .add(data)
            .addOnSuccessListener {
                _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                onSuccess()
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
    }
}