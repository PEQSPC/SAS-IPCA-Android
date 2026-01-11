package com.example.lojasocial.ui.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lojasocial.AppConstants
import com.example.lojasocial.R
import com.example.lojasocial.models.Products
import com.example.lojasocial.ui.theme.LojaSocialTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

// ---------------- STATE ----------------

data class ProductsListState(
    val products: List<Products> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ---------------- VIEWMODEL ----------------

class ProductsViewModel : ViewModel() {
    private val db = Firebase.firestore

    var uiState = mutableStateOf(ProductsListState())
        private set

    fun fetch() {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        db.collection("products")
            .addSnapshotListener { result, error ->
                if (error != null) {
                    uiState.value = uiState.value.copy(
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

                uiState.value = uiState.value.copy(
                    products = list.sortedBy { it.name ?: "" },
                    isLoading = false,
                    error = null
                )
            }
    }
}

// ---------------- BOTTOM NAV ----------------

private enum class BottomTab {
    Products, Beneficiaries, CreateBeneficiary, Admin
}

@Composable
private fun ProductsBottomBar(
    modifier: Modifier = Modifier,
    selected: BottomTab,
    onSelect: (BottomTab) -> Unit
) {
    NavigationBar(
        modifier = modifier.height(80.dp),
        containerColor = Color(0xFFDFF3E3),
        tonalElevation = 6.dp,
        windowInsets = WindowInsets(0)
    ) {
        BottomItem(BottomTab.Products, selected, Icons.Default.Inventory2, "Produtos", onSelect)
        BottomItem(BottomTab.Beneficiaries, selected, Icons.Default.Group, "Benef.", onSelect)
        BottomItem(BottomTab.CreateBeneficiary, selected, Icons.Default.PersonAdd, "Criar", onSelect)
        BottomItem(BottomTab.Admin, selected, Icons.Default.Home, "Admin", onSelect)
    }
}

@Composable
private fun RowScope.BottomItem(
    tab: BottomTab,
    selected: BottomTab,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onSelect: (BottomTab) -> Unit
) {
    val isSelected = tab == selected

    NavigationBarItem(
        selected = isSelected,
        onClick = { onSelect(tab) },
        alwaysShowLabel = true,
        icon = { Icon(icon, contentDescription = label) },
        label = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}

// ---------------- VIEW ----------------

@Composable
fun ProductsView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vm: ProductsViewModel = viewModel()
    val uiState = vm.uiState.value

    LaunchedEffect(Unit) { vm.fetch() }

    ProductsViewContent(
        navController = navController,
        modifier = modifier,
        uiState = uiState,
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
    onAddClick: () -> Unit = {},
    onProductClick: (Products) -> Unit = {}
) {
    val selectedTab = BottomTab.Products

    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.img),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ---------- CONTEÚDO (acima da bottom bar) ----------
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .padding(top = 20.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Produtos",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(14.dp))

                when {
                    uiState.isLoading -> {
                        Spacer(Modifier.weight(1f))
                        CircularProgressIndicator(color = Color.White)
                        Spacer(Modifier.weight(1f))
                    }

                    uiState.error != null -> {
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = uiState.error ?: "",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.weight(1f))
                    }

                    uiState.products.isEmpty() -> {
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "Sem produtos.",
                            color = Color.White
                        )
                        Spacer(Modifier.weight(1f))
                    }

                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
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

            // ---------- BOTTOM BAR ----------
            ProductsBottomBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                selected = selectedTab,
                onSelect = { tab ->
                    when (tab) {
                        BottomTab.Products -> navController.navigate(AppConstants.products) {
                            launchSingleTop = true
                        }

                        BottomTab.Beneficiaries -> navController.navigate(AppConstants.beneficiaries) {
                            launchSingleTop = true
                        }

                        BottomTab.CreateBeneficiary -> navController.navigate(AppConstants.createBeneficiary) {
                            launchSingleTop = true
                        }

                        BottomTab.Admin -> navController.navigate(AppConstants.adminHome) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        // ---------- FAB ----------
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 92.dp), // acima da bottom bar
            shape = CircleShape,
            containerColor = Color.White,
            contentColor = Color.Black
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar produto"
            )
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