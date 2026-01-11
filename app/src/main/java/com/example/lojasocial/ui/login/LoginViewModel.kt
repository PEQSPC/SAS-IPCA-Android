package com.example.lojasocial.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.AuthRepository
import com.example.lojasocial.models.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.lojasocial.models.ResultWrapper
import com.example.lojasocial.models.User
import com.example.lojasocial.models.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class LoginState(
    var username: String? = null,
    var password: String?  = null,
    var user : User? = null,
    var error: String? = null,
    var isLoading: Boolean? = null
)
@HiltViewModel
class LoginViewModel @Inject constructor(
    val authRepository: LoginRepository,
    val userRepository: UserRepository
): ViewModel() {

    var uiState = mutableStateOf(LoginState())
        private set

    fun setUsername(username: String) {
        uiState.value = uiState.value.copy(username = username)
    }

    fun setPassword(password: String) {
        uiState.value = uiState.value.copy(password = password)
    }


    fun login(onLoginSuccess: () -> Unit) {
        uiState.value = uiState.value.copy(isLoading = true)

        if (uiState.value.username.isNullOrEmpty()) {
            uiState.value = uiState.value.copy(
                error = "Username is required",
                isLoading = false
            )
        }

        if (uiState.value.password.isNullOrEmpty()) {
            uiState.value = uiState.value.copy(
                error = "Password is required",
                isLoading = false
            )
        }

        authRepository.login(
            uiState.value.username!!,
            uiState.value.password!!
        ).onEach {result ->
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        error = null,
                        isLoading = false
                    )
                    getUser(onLoginSuccess)
                }
                is ResultWrapper.Loading -> {
                    uiState.value = uiState.value.copy(
                        isLoading = true
                    )
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

    fun getUser(onLoginSuccess: () -> Unit) {

        val uid = FirebaseAuth.getInstance().uid!!

        userRepository.get(uid).onEach {result ->
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        user = result.data,
                        error = null,
                        isLoading = false
                    )
                    onLoginSuccess()
                }
                is ResultWrapper.Loading -> {
                    uiState.value = uiState.value.copy(
                        isLoading = true
                    )
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