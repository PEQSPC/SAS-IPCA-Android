package com.example.lojasocial.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.core.auth.AuthStateHolder
import com.example.lojasocial.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

// ---------- MODELS (mantém aqui, ou move para /models se preferires) ----------

data class News(
    val title: String,
    val body: String
) {
    companion object {
        fun fromJson(json: JSONObject): News {
            return News(
                title = json.optString("title", ""),
                body = json.optString("body", "")
            )
        }
    }
}

data class AdminStartState(
    val user: User? = null,
    val news: List<News> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ---------- VIEWMODEL ----------

@HiltViewModel
class AdminStartViewModel @Inject constructor(
    private val authStateHolder: AuthStateHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminStartState())
    val uiState: StateFlow<AdminStartState> = _uiState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authStateHolder.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(user = user)
            }
        }
    }

    fun loadNews(limit: Int = 5) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://dummyjson.com/posts?limit=$limit")
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao ligar à API"
                )
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Erro inesperado: ${response.code}"
                        )
                        return
                    }

                    val result = response.body?.string()
                    if (result.isNullOrBlank()) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Resposta vazia da API"
                        )
                        return
                    }

                    val jsonResult = try {
                        JSONObject(result)
                    } catch (e: Exception) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "JSON inválido"
                        )
                        return
                    }

                    // ✅ getJSONArray correto
                    val postsJsonArray = jsonResult.optJSONArray("posts")
                    if (postsJsonArray == null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Campo 'posts' não encontrado"
                        )
                        return
                    }

                    val newsList = mutableListOf<News>()
                    for (i in 0 until postsJsonArray.length()) {
                        val postJson = postsJsonArray.optJSONObject(i) ?: continue
                        newsList.add(News.fromJson(postJson))
                    }

                    _uiState.value = _uiState.value.copy(
                        news = newsList,
                        isLoading = false,
                        error = null
                    )
                }
            }
        })
    }

    fun logout() {
        authStateHolder.signOut()
    }
}