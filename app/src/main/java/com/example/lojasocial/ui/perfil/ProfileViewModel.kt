package com.example.lojasocial.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.core.auth.AuthStateHolder
import com.example.lojasocial.models.Profile
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class ProfileState(
    val profile: Profile = Profile(),
    val idadeText: String = "",   // data de nascimento DD/MM/YYYY
    val nifText: String = "",
    val photoUri: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authStateHolder: AuthStateHolder,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            val uid = authStateHolder.currentUser.value?.docId ?: run {
                _uiState.value = _uiState.value.copy(
                    error = "Utilizador não autenticado",
                    isLoading = false
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true)

            db.collection("profiles")
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val profile = doc.toObject(Profile::class.java) ?: Profile()
                    val birthDateStr = doc.getString("birthDate")

                    _uiState.value = _uiState.value.copy(
                        profile = profile,
                        idadeText = birthDateStr ?: "",
                        nifText = profile.nif?.toString() ?: "",
                        photoUri = doc.getString("photoUri"),
                        isLoading = false,
                        error = null,
                        isSaved = false
                    )
                }
                .addOnFailureListener {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = it.message
                    )
                }
        }
    }

    fun setNome(v: String) {
        _uiState.value = _uiState.value.copy(
            profile = _uiState.value.profile.copy(nome = v),
            isSaved = false
        )
    }

    fun setSobrenome(v: String) {
        _uiState.value = _uiState.value.copy(
            profile = _uiState.value.profile.copy(sobrenome = v),
            isSaved = false
        )
    }

    fun setGenero(v: String) {
        _uiState.value = _uiState.value.copy(
            profile = _uiState.value.profile.copy(genero = v),
            isSaved = false
        )
    }

    fun setIdadeText(v: String) {
        _uiState.value = _uiState.value.copy(
            idadeText = v,
            isSaved = false
        )
    }

    fun setNifText(v: String) {
        _uiState.value = _uiState.value.copy(
            nifText = v,
            isSaved = false
        )
    }

    fun setPhotoUri(uri: String?) {
        _uiState.value = _uiState.value.copy(
            photoUri = uri,
            isSaved = false
        )
    }

    // ✔ Método compatível com Android API 24
    private fun calcularIdade(data: String): Int? {
        try {
            val partes = data.split("/")
            if (partes.size != 3) return null

            val dia = partes[0].toInt()
            val mes = partes[1].toInt() - 1
            val ano = partes[2].toInt()

            val hoje = Calendar.getInstance()
            val nasc = Calendar.getInstance()

            nasc.set(ano, mes, dia)

            var idade = hoje.get(Calendar.YEAR) - nasc.get(Calendar.YEAR)

            if (hoje.get(Calendar.DAY_OF_YEAR) < nasc.get(Calendar.DAY_OF_YEAR)) {
                idade--
            }

            return idade
        } catch (_: Exception) {
            return null
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val uid = authStateHolder.currentUser.value?.docId ?: run {
                _uiState.value = _uiState.value.copy(error = "Utilizador não autenticado")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true)

            val idadeCalculada = calcularIdade(_uiState.value.idadeText)
            val nifInt = _uiState.value.nifText.toIntOrNull()

            val profileToSave = _uiState.value.profile.copy(
                idade = idadeCalculada,
                nif = nifInt
            )

            val dataMap = hashMapOf(
                "nome" to profileToSave.nome,
                "sobrenome" to profileToSave.sobrenome,
                "genero" to profileToSave.genero,
                "idade" to profileToSave.idade,
                "nif" to profileToSave.nif,
                "birthDate" to _uiState.value.idadeText,   // guardamos a data real
                "photoUri" to _uiState.value.photoUri
            )

            db.collection("profiles")
                .document(uid)
                .set(dataMap)
                .addOnSuccessListener {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSaved = true,
                        error = null
                    )
                }
                .addOnFailureListener {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSaved = false,
                        error = it.message
                    )
                }
        }
    }
}