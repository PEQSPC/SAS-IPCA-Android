package com.example.lojasocial.ui.perfil

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.Calendar

data class ProfileState(
    val profile: Profile = Profile(),
    val idadeText: String = "",   // data de nascimento DD/MM/YYYY
    val nifText: String = "",
    val photoUri: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

class ProfileViewModel : ViewModel() {

    var uiState = mutableStateOf(ProfileState())
        private set

    private val db = Firebase.firestore

    fun loadProfile() {
        val uid = Firebase.auth.currentUser?.uid ?: run {
            uiState.value = uiState.value.copy(
                error = "Utilizador não autenticado",
                isLoading = false
            )
            return
        }

        uiState.value = uiState.value.copy(isLoading = true)

        db.collection("profiles")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val profile = doc.toObject(Profile::class.java) ?: Profile()
                val birthDateStr = doc.getString("birthDate")

                uiState.value = uiState.value.copy(
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
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = it.message
                )
            }
    }

    fun setNome(v: String) {
        uiState.value = uiState.value.copy(
            profile = uiState.value.profile.copy(nome = v),
            isSaved = false
        )
    }

    fun setSobrenome(v: String) {
        uiState.value = uiState.value.copy(
            profile = uiState.value.profile.copy(sobrenome = v),
            isSaved = false
        )
    }

    fun setGenero(v: String) {
        uiState.value = uiState.value.copy(
            profile = uiState.value.profile.copy(genero = v),
            isSaved = false
        )
    }

    fun setIdadeText(v: String) {
        uiState.value = uiState.value.copy(
            idadeText = v,
            isSaved = false
        )
    }

    fun setNifText(v: String) {
        uiState.value = uiState.value.copy(
            nifText = v,
            isSaved = false
        )
    }

    fun setPhotoUri(uri: String?) {
        uiState.value = uiState.value.copy(
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
        val uid = Firebase.auth.currentUser?.uid ?: run {
            uiState.value = uiState.value.copy(error = "Utilizador não autenticado")
            return
        }

        uiState.value = uiState.value.copy(isLoading = true)

        val idadeCalculada = calcularIdade(uiState.value.idadeText)
        val nifInt = uiState.value.nifText.toIntOrNull()

        val profileToSave = uiState.value.profile.copy(
            idade = idadeCalculada,
            nif = nifInt
        )

        val dataMap = hashMapOf(
            "nome" to profileToSave.nome,
            "sobrenome" to profileToSave.sobrenome,
            "genero" to profileToSave.genero,
            "idade" to profileToSave.idade,
            "nif" to profileToSave.nif,
            "birthDate" to uiState.value.idadeText,   // guardamos a data real
            "photoUri" to uiState.value.photoUri
        )

        db.collection("profiles")
            .document(uid)
            .set(dataMap)
            .addOnSuccessListener {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    isSaved = true,
                    error = null
                )
            }
            .addOnFailureListener {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    isSaved = false,
                    error = it.message
                )
            }
    }
}