package com.example.lojasocial.ui.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.core.auth.AuthStateHolder
import com.example.lojasocial.models.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.lojasocial.models.ResultWrapper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class LoginState(
    var username: String? = null,
    var password: String?  = null,
    var error: String? = null,
    var isLoading: Boolean? = null
)
@HiltViewModel
class LoginViewModel @Inject constructor(
    val authRepository: LoginRepository,
    private val authStateHolder: AuthStateHolder
): ViewModel() {

    var uiState = mutableStateOf(LoginState())
        private set

    fun setUsername(username: String) {
        uiState.value = uiState.value.copy(username = username)
    }

    fun setPassword(password: String) {
        uiState.value = uiState.value.copy(password = password)
    }


    fun login() {
        Log.d("LoginViewModel", "login() iniciado - username: ${uiState.value.username}")
        uiState.value = uiState.value.copy(isLoading = true)

        if (uiState.value.username.isNullOrEmpty()) {
            Log.w("LoginViewModel", "login() falhou - username vazio")
            uiState.value = uiState.value.copy(
                error = "Username is required",
                isLoading = false
            )
            return
        }

        if (uiState.value.password.isNullOrEmpty()) {
            Log.w("LoginViewModel", "login() falhou - password vazio")
            uiState.value = uiState.value.copy(
                error = "Password is required",
                isLoading = false
            )
            return
        }

        val username = uiState.value.username
        val password = uiState.value.password

        if (username == null || password == null) {
            Log.w("LoginViewModel", "login() falhou - username ou password NULL")
            uiState.value = uiState.value.copy(
                error = "Username e password são obrigatórios",
                isLoading = false
            )
            return
        }

        Log.d("LoginViewModel", "Chamando authRepository.login()")
        authRepository.login(username, password).onEach {result ->
            when(result){
                is ResultWrapper.Success -> {
                    Log.d("LoginViewModel", "authRepository.login() SUCCESS - refreshing AuthStateHolder")
                    uiState.value = uiState.value.copy(
                        error = null,
                        isLoading = false
                    )
                    // AuthStateHolder will automatically load user data and trigger navigation
                    authStateHolder.refresh()
                }
                is ResultWrapper.Loading -> {
                    Log.d("LoginViewModel", "authRepository.login() LOADING")
                    uiState.value = uiState.value.copy(
                        isLoading = true
                    )
                }
                is ResultWrapper.Error -> {
                    Log.e("LoginViewModel", "authRepository.login() ERROR: ${result.message}")
                    uiState.value = uiState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}