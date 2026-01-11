package com.example.lojasocial.ui.admin

import androidx.compose.foundation.Image
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
import com.example.lojasocial.AppConstansts
import com.example.lojasocial.R
import com.example.lojasocial.models.Product
import com.example.lojasocial.ui.Start.ProductState
import com.example.lojasocial.ui.Start.StartProductViewModel
import com.example.lojasocial.ui.theme.LojaSocialTheme

// ✅ removi Stats e adicionei Agendas
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

    LaunchedEffect(Unit) { productViewModel.loadProduct(productId = 1) }

    AdminStartViewContent(
        modifier = modifier,
        uiState = uiState,

        // ✅ ROTAS
        onProductsClick = {
            navController.navigate(AppConstansts.products) { launchSingleTop = true }
        },
        onBeneficiariesClick = {
            navController.navigate(AppConstansts.beneficiaries) { launchSingleTop = true }
        },
        onCreateBeneficiaryClick = {
            navController.navigate(AppConstansts.createBeneficiary) { launchSingleTop = true }
        },
        onFamiliesClick = {
            navController.navigate(AppConstansts.families) { launchSingleTop = true }
        },
        onAgendasClick = {
            navController.navigate(AppConstansts.agendas) { launchSingleTop = true }
        },
        onProfileClick = {
            navController.navigate(AppConstansts.profile) { launchSingleTop = true }
        }
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

    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.img),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

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

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when {
                        uiState.isLoading -> {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("A carregar produto...", color = Color.White)
                        }

                        uiState.error != null -> {
                            Text(
                                text = uiState.error ?: "",
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                        }

                        uiState.product != null -> {
                            val product = uiState.product

                            Text(
                                text = "Produto em destaque",
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            AsyncImage(
                                model = product.thumbnail,
                                contentDescription = product.title,
                                modifier = Modifier
                                    .height(140.dp)
                                    .padding(bottom = 12.dp),
                                contentScale = ContentScale.Crop
                            )

                            Text(
                                text = product.title ?: "",
                                color = Color.Black,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Categoria: ${product.category ?: "-"}",
                                color = Color.Black
                            )

                            Text(
                                text = "Preço: ${product.price ?: 0.0} €",
                                color = Color.Black
                            )

                            if (!product.description.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = product.description!!,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        else -> {
                            Text("Nenhum produto disponível.", color = Color.Black)
                        }
                    }
                }
            }

            AdminBottomBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
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
private fun AdminBottomBar(
    modifier: Modifier = Modifier,
    selected: AdminTab,
    onSelect: (AdminTab) -> Unit
) {
    NavigationBar(
        modifier = modifier.height(80.dp),
        containerColor = Color(0xFFDFF3E3),
        tonalElevation = 6.dp,
        windowInsets = WindowInsets(0)
    ) {
        AdminBottomItem(AdminTab.Products, selected, Icons.Default.Inventory2, "Produtos", onSelect)
        AdminBottomItem(AdminTab.Beneficiaries, selected, Icons.Default.Group, "Beneficiários", onSelect)
        AdminBottomItem(AdminTab.Create, selected, Icons.Default.AddCircle, "Criar", onSelect)
        AdminBottomItem(AdminTab.Families, selected, Icons.Default.Folder, "Famílias", onSelect)

        // ✅ NOVO: AGENDAS (em vez de Estatísticas)
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
        alwaysShowLabel = true,
        icon = { Icon(icon, contentDescription = label) },
        label = {
            Text(
                text = label,
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