package com.example.lojasocial.ui.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.LoginRepository
import com.example.lojasocial.models.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class RegisterState(
    var username: String? = null,
    var password: String? = null,
    var email: String? = null,
    var error: String? = null,
    var isLoading: Boolean? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: LoginRepository
) : ViewModel() {

    var uiState = mutableStateOf(RegisterState())
        private set

    fun setUsername(username: String) {
        uiState.value = uiState.value.copy(username = username)
    }

    fun setEmail(email: String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun setPassword(password: String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun register(onRegisterSuccess: () -> Unit) {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        if (uiState.value.username.isNullOrEmpty()) {
            uiState.value = uiState.value.copy(
                error = "Username is required",
                isLoading = false
            )
            return
        }

        if (uiState.value.email.isNullOrEmpty()) {
            uiState.value = uiState.value.copy(
                error = "Email is required",
                isLoading = false
            )
            return
        }

        if (uiState.value.password.isNullOrEmpty()) {
            uiState.value = uiState.value.copy(
                error = "Password is required",
                isLoading = false
            )
            return
        }

        val email = uiState.value.email
        val password = uiState.value.password

        if (email == null || password == null) {
            uiState.value = uiState.value.copy(
                error = "Email e password são obrigatórios",
                isLoading = false
            )
            return
        }

        authRepository.register(email, password).onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    onRegisterSuccess()
                    uiState.value = uiState.value.copy(
                        error = null,
                        isLoading = false
                    )
                }

                is ResultWrapper.Loading -> {
                    uiState.value = uiState.value.copy(isLoading = true)
                }

                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}