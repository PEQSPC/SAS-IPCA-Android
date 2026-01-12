package com.example.lojasocial.ui.home

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.AppConstants
import com.example.lojasocial.R

@Composable
fun UserHomeView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: UserHomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    UserHomeViewContent(
        modifier = modifier,
        uiState = uiState,
        onProductsClick = {
            navController.navigate(AppConstants.products) {
                launchSingleTop = true
            }
        },
        onProfileClick = {
            navController.navigate(AppConstants.profile) {
                launchSingleTop = true
            }
        },
        onStockClick = {
            navController.navigate(AppConstants.carts) {
                launchSingleTop = true
            }
        },
        onLogoutClick = {
            viewModel.logout()
            // Navigation will be handled automatically by RootNavGraph
        }
    )
}

@Composable
fun UserHomeViewContent(
    modifier: Modifier = Modifier,
    uiState: UserHomeState,
    onProductsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onStockClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 60.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ipca1),
                    contentDescription = "IPCA Logo",
                    modifier = Modifier.height(60.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(24.dp))

                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(color = Color.White)
                    }
                    uiState.error != null -> {
                        Text(
                            text = uiState.error ?: "",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        Text(
                            text = "Bem-vindo${if (!uiState.user?.name.isNullOrEmpty()) ", ${uiState.user?.name}" else ""}!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Loja Social IPCA",
                            fontSize = 16.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Menu cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MenuCard(
                    icon = Icons.Default.Inventory2,
                    title = "Produtos",
                    description = "Ver produtos disponíveis",
                    onClick = onProductsClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                MenuCard(
                    icon = Icons.Default.ShoppingCart,
                    title = "Stock",
                    description = "Gerir inventário",
                    onClick = onStockClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                MenuCard(
                    icon = Icons.Default.Person,
                    title = "Perfil",
                    description = "Ver e editar perfil",
                    onClick = onProfileClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                MenuCard(
                    icon = Icons.Default.ExitToApp,
                    title = "Sair",
                    description = "Terminar sessão",
                    onClick = onLogoutClick,
                    containerColor = Color(0xFFEF5350)
                )
            }

            // Footer logos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ipca),
                    contentDescription = "IPCA",
                    modifier = Modifier.height(20.dp),
                    contentScale = ContentScale.Fit
                )

                Image(
                    painter = painterResource(id = R.drawable.sassocial),
                    contentDescription = "Serviços de Ação Social",
                    modifier = Modifier.height(30.dp),
                    contentScale = ContentScale.Fit
                )

                Image(
                    painter = painterResource(id = R.drawable.est),
                    contentDescription = "EST",
                    modifier = Modifier.height(30.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
private fun MenuCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    containerColor: Color = Color(0xFFDFF3E3)
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp),
                tint = Color(0xFF2E7D32)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B1220)
                )

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color(0xFF0B1220).copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF2E7D32)
            )
        }
    }
}
