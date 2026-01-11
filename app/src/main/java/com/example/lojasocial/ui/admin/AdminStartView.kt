package com.example.lojasocial.ui.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lojasocial.AppConstants
import com.example.lojasocial.R
import com.example.lojasocial.models.Product
import com.example.lojasocial.ui.Start.ProductState
import com.example.lojasocial.ui.Start.StartProductViewModel
import com.example.lojasocial.ui.theme.LojaSocialTheme

private enum class AdminTab {
    Products, Beneficiaries, Create, Families, Agendas, Profile
}

@Composable
fun AdminStartView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val productViewModel: StartProductViewModel = viewModel()
    val uiState by productViewModel.uiState

    LaunchedEffect(Unit) {
        productViewModel.loadProduct(productId = 1)
    }

    AdminStartViewContent(
        modifier = modifier,
        uiState = uiState,
        onProductsClick = { navController.navigate(AppConstants.products) },
        onBeneficiariesClick = { navController.navigate(AppConstants.beneficiaries) },
        onCreateBeneficiaryClick = { navController.navigate(AppConstants.createBeneficiary) },
        onFamiliesClick = { navController.navigate(AppConstants.families) },
        onAgendasClick = { navController.navigate(AppConstants.agendas) },
        onProfileClick = { navController.navigate(AppConstants.profile) }
    )
}

@Composable
fun AdminStartViewContent(
    modifier: Modifier = Modifier,
    uiState: ProductState,
    onProductsClick: () -> Unit = {},
    onBeneficiariesClick: () -> Unit = {},
    onCreateBeneficiaryClick: () -> Unit = {},
    onFamiliesClick: () -> Unit = {},
    onAgendasClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(AdminTab.Products) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(80.dp))

            Image(
                painter = painterResource(id = R.drawable.ipca1),
                contentDescription = "IPCA",
                modifier = Modifier.height(56.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("A carregar produto…")
                    }

                    uiState.error != null -> {
                        Text(
                            text = uiState.error,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }

                    uiState.product != null -> {
                        ProductCard(uiState.product)
                    }

                    else -> {
                        Text("Nenhum produto disponível.")
                    }
                }
            }

            AdminBottomBar(
                modifier = Modifier.fillMaxWidth(),
                selected = selectedTab,
                onSelect = { tab ->
                    selectedTab = tab
                    when (tab) {
                        AdminTab.Products -> onProductsClick()
                        AdminTab.Beneficiaries -> onBeneficiariesClick()
                        AdminTab.Create -> onCreateBeneficiaryClick()
                        AdminTab.Families -> onFamiliesClick()
                        AdminTab.Agendas -> onAgendasClick()
                        AdminTab.Profile -> onProfileClick()
                    }
                }
            )
        }
    }
}

@Composable
private fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Produto em destaque",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
                    .background(Color.White),
                contentScale = ContentScale.Fit,
                placeholder = painterResource(R.drawable.ic_placeholder),
                error = painterResource(R.drawable.ic_error)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                product.title ?: "",
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Text("Categoria: ${product.category ?: "-"}")
            Text("Preço: ${product.price ?: 0.0} €")

            product.description?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(it, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun AdminBottomBar(
    modifier: Modifier = Modifier,
    selected: AdminTab,
    onSelect: (AdminTab) -> Unit
) {
    NavigationBar(
        modifier = modifier.height(80.dp),
        containerColor = Color(0xFFDFF3E3),
        tonalElevation = 6.dp
    ) {
        AdminBottomItem(AdminTab.Products, selected, Icons.Default.Inventory2, "Produtos", onSelect)
        AdminBottomItem(AdminTab.Beneficiaries, selected, Icons.Default.Group, "Beneficiários", onSelect)
        AdminBottomItem(AdminTab.Create, selected, Icons.Default.AddCircle, "Criar", onSelect)
        AdminBottomItem(AdminTab.Families, selected, Icons.Default.Folder, "Famílias", onSelect)
        AdminBottomItem(AdminTab.Agendas, selected, Icons.Default.CalendarMonth, "Agendas", onSelect)
        AdminBottomItem(AdminTab.Profile, selected, Icons.Default.Person, "Perfil", onSelect)
    }
}

@Composable
private fun RowScope.AdminBottomItem(
    tab: AdminTab,
    selected: AdminTab,
    icon: ImageVector,
    label: String,
    onSelect: (AdminTab) -> Unit
) {
    NavigationBarItem(
        selected = tab == selected,
        onClick = { onSelect(tab) },
        icon = { Icon(icon, contentDescription = label) },
        label = {
            Text(
                label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AdminStartViewPreview() {
    LojaSocialTheme {
        AdminStartViewContent(
            uiState = ProductState(
                product = Product(
                    title = "Produto Exemplo",
                    category = "Categoria X",
                    description = "Descrição de teste",
                    price = 19.99,
                    thumbnail = "https://dummyjson.com/image/i/products/1/thumbnail.jpg"
                ),
                isLoading = false,
                error = null
            )
        )
    }
}
