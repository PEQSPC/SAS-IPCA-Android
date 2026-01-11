package com.example.lojasocial.ui.carts


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.Cart
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


data class CartsState(
    val carts: List<Cart> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false
)

class CartsViewModel : ViewModel() {

    var uiState = mutableStateOf(CartsState())
        private set

    private val db = Firebase.firestore

    fun fetchCarts() {
        val uid = Firebase.auth.currentUser?.uid
        if (uid == null) {
            uiState.value = uiState.value.copy(
                error = "Utilizador não autenticado",
                isLoading = false
            )
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, error = null)

        db.collection("carts")
            .whereArrayContains("owners", uid)
            .addSnapshotListener { result, error ->
                if (error != null) {
                    uiState.value = uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                    return@addSnapshotListener
                }

                val carts = mutableListOf<Cart>()
                for (document in result?.documents ?: emptyList()) {
                    val cart = document.toObject(Cart::class.java)
                    cart?.docId = document.id
                    cart?.let { carts.add(it) }
                }

                uiState.value = uiState.value.copy(
                    carts = carts,
                    error = null,
                    isLoading = false
                )
            }
    }

    fun addCart() {
        val uid = Firebase.auth.currentUser?.uid
        if (uid == null) {
            uiState.value = uiState.value.copy(
                error = "Utilizador não autenticado"
            )
            return
        }

        uiState.value = uiState.value.copy(isLoading = true)

        val newCart = Cart(
            name = "New Cart ${uiState.value.carts.size + 1}",
            owners = listOf(uid)
        )

        db.collection("carts")
            .add(newCart)
            .addOnSuccessListener {

                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = null
                )
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
    }
}