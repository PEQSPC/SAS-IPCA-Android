package com.example.lojasocial.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.core.auth.AuthState
import com.example.lojasocial.core.auth.AuthStateHolder
import com.example.lojasocial.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserHomeState(
    var user: User? = null,
    var isLoading: Boolean = false,
    var error: String? = null
)

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    private val authStateHolder: AuthStateHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserHomeState())
    val uiState: StateFlow<UserHomeState> = _uiState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authStateHolder.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(
                    user = user,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    fun logout() {
        authStateHolder.signOut()
    }
}
