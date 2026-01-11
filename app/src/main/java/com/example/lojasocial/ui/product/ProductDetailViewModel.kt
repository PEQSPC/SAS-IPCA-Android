package com.example.lojasocial.ui.products

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.Products
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

data class ProductDetailState(
    var sku: String = "",
    var name: String = "",
    var familia: String = "",
    var unidade: String = "",
    var localizacao: String = "",
    var stockMinimo: String = "",
    var stockAtual: String = "0",
    var eans: String = "",
    var notas: String = "",
    var error: String? = null,
    var isLoading: Boolean = false
)

class ProductDetailViewModel : ViewModel() {

    private val db = Firebase.firestore
    private var docId: String? = null

    var uiState = mutableStateOf(ProductDetailState())
        private set

    fun setDocId(id: String?) {
        docId = id?.trim()
            ?.takeIf { it.isNotBlank() && it != "{docId}" }
    }

    /** ✅ CHAMAR quando abres o ecrã para criar */
    fun resetForCreate() {
        docId = null
        uiState.value = ProductDetailState() // limpa todos os campos
    }

    fun setSku(v: String) { uiState.value = uiState.value.copy(sku = v, error = null) }
    fun setName(v: String) { uiState.value = uiState.value.copy(name = v, error = null) }
    fun setFamilia(v: String) { uiState.value = uiState.value.copy(familia = v, error = null) }
    fun setUnidade(v: String) { uiState.value = uiState.value.copy(unidade = v, error = null) }
    fun setLocalizacao(v: String) { uiState.value = uiState.value.copy(localizacao = v, error = null) }
    fun setStockMinimo(v: String) { uiState.value = uiState.value.copy(stockMinimo = v.filter { it.isDigit() }, error = null) }
    fun setStockAtual(v: String) { uiState.value = uiState.value.copy(stockAtual = v.filter { it.isDigit() }, error = null) }
    fun setEans(v: String) { uiState.value = uiState.value.copy(eans = v, error = null) }
    fun setNotas(v: String) { uiState.value = uiState.value.copy(notas = v, error = null) }

    fun addEanScanned(code: String) {
        val cleaned = code.trim()
        if (cleaned.isBlank()) return

        val list = uiState.value.eans
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toMutableList()

        if (!list.contains(cleaned)) list.add(cleaned)

        uiState.value = uiState.value.copy(eans = list.joinToString(", "), error = null)
    }

    fun fetch(productDocId: String) {
        docId = productDocId
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        db.collection("products")
            .document(productDocId)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = "Produto não encontrado"
                    )
                    return@addOnSuccessListener
                }

                val stockMin = (doc.get("stockMinimo") as? Number)?.toInt()
                    ?: doc.getString("stockMinimo")?.toIntOrNull()
                    ?: 0

                val stockAt = (doc.get("stockAtual") as? Number)?.toInt()
                    ?: doc.getString("stockAtual")?.toIntOrNull()
                    ?: 0

                uiState.value = uiState.value.copy(
                    sku = doc.getString("sku") ?: "",
                    name = doc.getString("name") ?: "",
                    familia = doc.getString("familia") ?: "",
                    unidade = doc.getString("unidade") ?: "",
                    localizacao = doc.getString("localizacao") ?: "",
                    stockMinimo = stockMin.toString(),
                    stockAtual = stockAt.toString(),
                    eans = doc.getString("eans") ?: "",
                    notas = doc.getString("notas") ?: "",
                    isLoading = false,
                    error = null
                )
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.message)
            }
    }

    private fun validate(): Boolean {
        if (uiState.value.sku.isBlank()) { uiState.value = uiState.value.copy(error = "SKU é obrigatório"); return false }
        if (uiState.value.name.isBlank()) { uiState.value = uiState.value.copy(error = "Nome é obrigatório"); return false }
        if (uiState.value.familia.isBlank()) { uiState.value = uiState.value.copy(error = "Família é obrigatória"); return false }
        if (uiState.value.unidade.isBlank()) { uiState.value = uiState.value.copy(error = "Unidade é obrigatória"); return false }
        return true
    }

    fun save(onSuccess: () -> Unit = {}) {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        if (!validate()) {
            uiState.value = uiState.value.copy(isLoading = false)
            return
        }

        val payload = Products(
            sku = uiState.value.sku,
            name = uiState.value.name,
            familia = uiState.value.familia,
            unidade = uiState.value.unidade,
            localizacao = uiState.value.localizacao,
            stockMinimo = uiState.value.stockMinimo.toIntOrNull() ?: 0,
            stockAtual = uiState.value.stockAtual.toIntOrNull() ?: 0,
            eans = uiState.value.eans,
            notas = uiState.value.notas
        )

        val currentId = docId

        if (currentId == null) {
            db.collection("products")
                .add(payload)
                .addOnSuccessListener {
                    uiState.value = uiState.value.copy(isLoading = false, error = null)
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    uiState.value = uiState.value.copy(isLoading = false, error = e.message)
                }
        } else {
            db.collection("products")
                .document(currentId)
                .set(payload, SetOptions.merge())
                .addOnSuccessListener {
                    uiState.value = uiState.value.copy(isLoading = false, error = null)
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    uiState.value = uiState.value.copy(isLoading = false, error = e.message)
                }
        }
    }

    fun delete(onDeleted: () -> Unit = {}) {
        val id = docId ?: return
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        db.collection("products")
            .document(id)
            .delete()
            .addOnSuccessListener {
                uiState.value = uiState.value.copy(isLoading = false, error = null)
                onDeleted()
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.message)
            }
    }
}