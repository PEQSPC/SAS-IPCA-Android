package com.example.lojasocial.ui.admin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

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
    val news: List<News> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ---------- VIEWMODEL ----------

class AdminStartViewModel : ViewModel() {

    var uiState = mutableStateOf(AdminStartState())
        private set

    fun loadNews(limit: Int = 5) {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://dummyjson.com/posts?limit=$limit")
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao ligar à API"
                )
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = "Erro inesperado: ${response.code}"
                        )
                        return
                    }

                    val result = response.body?.string()
                    if (result.isNullOrBlank()) {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = "Resposta vazia da API"
                        )
                        return
                    }

                    val jsonResult = try {
                        JSONObject(result)
                    } catch (e: Exception) {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = "JSON inválido"
                        )
                        return
                    }

                    // ✅ getJSONArray correto
                    val postsJsonArray = jsonResult.optJSONArray("posts")
                    if (postsJsonArray == null) {
                        uiState.value = uiState.value.copy(
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

                    uiState.value = uiState.value.copy(
                        news = newsList,
                        isLoading = false,
                        error = null
                    )
                }
            }
        })
    }
}