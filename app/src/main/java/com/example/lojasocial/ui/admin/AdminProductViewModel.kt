package com.example.lojasocial.ui.admin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.Product
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

data class AdminProductState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdminProductViewModel : ViewModel() {

    var uiState = mutableStateOf(AdminProductState())
        private set

    fun loadFeaturedProduct(productId: Int = 1) {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://dummyjson.com/products/$productId")
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

                    val jsonString = response.body?.string()
                    if (jsonString.isNullOrBlank()) {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = "Resposta vazia da API"
                        )
                        return
                    }

                    val jsonObject = JSONObject(jsonString)

                    val product = try {
                        Product.fromJson(jsonObject)
                    } catch (e: Exception) {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = e.message ?: "Erro a converter JSON"
                        )
                        return
                    }

                    uiState.value = uiState.value.copy(
                        product = product,
                        isLoading = false,
                        error = null
                    )
                }
            }
        })
    }
}