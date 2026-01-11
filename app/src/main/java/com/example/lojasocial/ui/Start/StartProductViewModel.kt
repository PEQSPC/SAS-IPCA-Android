package com.example.lojasocial.ui.Start

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.Product
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

data class ProductState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class StartProductViewModel : ViewModel() {

    var uiState = mutableStateOf(ProductState())
        private set

    fun loadProduct(productId: Int = 1) {  // Podes escolher outro ID
        uiState.value = uiState.value.copy(isLoading = true)

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://dummyjson.com/products/$productId")
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
                            error = "Erro inesperado: $response"
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

                    val jsonString = body.string()
                    val jsonObject = JSONObject(jsonString)

                    val product = Product.fromJson(jsonObject)

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