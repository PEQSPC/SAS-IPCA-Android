package com.example.lojasocial.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.lojasocial.AppConstants
import com.example.lojasocial.models.Products
import com.example.lojasocial.ui.theme.LojaSocialTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// ---------------- STATE ----------------

data class ProductsListState(
    val products: List<Products> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ---------------- VIEWMODEL ----------------

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private var listener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(ProductsListState())
    val uiState: StateFlow<ProductsListState> = _uiState.asStateFlow()

    fun fetch() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        listener?.remove()
        listener = db.collection("products")
            .addSnapshotListener { result, error ->
                if (error != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                    return@addSnapshotListener
                }

                val list = mutableListOf<Products>()
                for (doc in result?.documents ?: emptyList()) {
                    val p = doc.toObject(Products::class.java)
                    p?.docId = doc.id
                    if (p != null) list.add(p)
                }

                _uiState.value = _uiState.value.copy(
                    products = list.sortedBy { it.name ?: "" },
                    isLoading = false,
                    error = null
                )
            }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}

// ---------------- VIEW ----------------

@Composable
fun ProductsView(
    navController: NavController,
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false
) {
    val vm: ProductsViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) { vm.fetch() }

    ProductsViewContent(
        navController = navController,
        modifier = modifier,
        uiState = uiState,
        isAdmin = isAdmin,
        onAddClick = {
            navController.navigate(AppConstants.productsDetailCreate) {
                launchSingleTop = true
            }
        },
        onProductClick = { product ->
            val id = product.docId?.trim().orEmpty()
            if (id.isNotBlank()) {
                navController.navigate(
                    AppConstants.productsDetail.replace("{docId}", id)
                ) {
                    launchSingleTop = true
                }
            }
        }
    )
}

@Composable
fun ProductsViewContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    uiState: ProductsListState,
    isAdmin: Boolean = false,
    onAddClick: () -> Unit = {},
    onProductClick: (Products) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            Text(
                text = "Produtos",
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2E7D32))
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error ?: "",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                uiState.products.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sem produtos.",
                            color = Color.Black
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(uiState.products) { p ->
                            ProductRow(
                                product = p,
                                onClick = { onProductClick(p) }
                            )
                        }
                    }
                }
            }
        }

        // ---------- FAB (só para admin) ----------
        if (isAdmin) {
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp),
                shape = CircleShape,
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar produto"
                )
            }
        }
    }
}

// ---------------- ROW ----------------

@Composable
private fun ProductRow(
    product: Products,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F6F6))
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = product.name ?: "(Sem nome)",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "SKU: ${product.sku ?: "-"}",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// ---------------- PREVIEW ----------------

@Preview(showBackground = true)
@Composable
fun ProductsViewPreview() {
    LojaSocialTheme {
        ProductsViewContent(
            navController = androidx.navigation.compose.rememberNavController(),
            uiState = ProductsListState(
                products = listOf(
                    Products(docId = "1", sku = "A1", name = "Arroz"),
                    Products(docId = "2", sku = "B2", name = "Massa")
                ),
                isLoading = false
            )
        )
    }
}