package com.example.lojasocial.ui.Start

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

data class News(
    val title: String,
    val body: String
) {
    companion object {
        fun fromJson(json: JSONObject): News {
            return News(
                title = json.getString("title"),
                body = json.getString("body")
            )
        }
    }
}

data class NewsListState(
    val news: List<News> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class StartViewModel : ViewModel() {

    var uiState = mutableStateOf(NewsListState())
        private set

    fun loadNews() {
        uiState.value = uiState.value.copy(isLoading = true)

        val client = OkHttpClient()

        // API de exemplo – posts tratados como notícias
        val request = Request.Builder()
            .url("https://dummyjson.com/posts?limit=5")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = "Unexpected code $response"
                        )
                        return
                    }

                    val body = response.body
                    if (body == null) {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = "Resposta vazia do servidor"
                        )
                        return
                    }

                    val result = body.string()
                    val jsonResult = JSONObject(result)

                    // 👇 É ISTO: getJSONArray, tudo junto, sem caracteres estranhos
                    val postsJsonArray = jsonResult.getJSONArray("posts")

                    val newsList = mutableListOf<News>()
                    for (i in 0 until postsJsonArray.length()) {
                        val postJson = postsJsonArray.getJSONObject(i)
                        val news = News.fromJson(postJson)
                        newsList.add(news)
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