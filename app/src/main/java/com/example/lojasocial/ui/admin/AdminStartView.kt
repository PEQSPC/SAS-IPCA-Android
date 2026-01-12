package com.example.lojasocial.ui.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.AppConstants
import com.example.lojasocial.R
import com.example.lojasocial.models.User
import com.example.lojasocial.ui.admin.components.StatsCard
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun AdminStartView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: AdminStartViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboardStats()
    }

    AdminStartViewContent(
        modifier = modifier,
        uiState = uiState,
        onStockClick = { navController.navigate(AppConstants.stock) },
        onDonationsClick = { navController.navigate(AppConstants.donations) },
        onBeneficiariesClick = { navController.navigate(AppConstants.beneficiaries) },
        onDeliveriesClick = { navController.navigate(AppConstants.deliveries) },
        onRefresh = { viewModel.loadDashboardStats() }
    )
}

@Composable
fun AdminStartViewContent(
    modifier: Modifier = Modifier,
    uiState: AdminStartState,
    onStockClick: () -> Unit = {},
    onDonationsClick: () -> Unit = {},
    onBeneficiariesClick: () -> Unit = {},
    onDeliveriesClick: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    uiState.user?.let { user ->
                        Text(
                            text = "Olá, ${user.name}!",
                            fontSize = 14.sp,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    }
                }

                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Atualizar",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Loading State
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2E7D32))
                }
            }

            // Error State
            else if (uiState.error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Erro ao carregar dados",
                            color = Color(0xFFF44336),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = uiState.error,
                            color = Color(0xFFF44336),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRefresh) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }

            // Dashboard Content
            else {
                // Indicators Grid (2x2)
                Text(
                    text = "Indicadores",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsCard(
                        title = "Artigos",
                        value = uiState.dashboardStats?.totalItems?.toString() ?: "0",
                        subtitle = "${uiState.dashboardStats?.itemsInAlert ?: 0} em alerta",
                        icon = Icons.Default.Inventory2,
                        iconTint = if ((uiState.dashboardStats?.itemsInAlert ?: 0) > 0)
                            Color(0xFFFFA726) else Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f)
                    )

                    StatsCard(
                        title = "Doações",
                        value = uiState.dashboardStats?.totalDonations?.toString() ?: "0",
                        subtitle = "Este mês",
                        icon = Icons.Default.CardGiftcard,
                        iconTint = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsCard(
                        title = "Beneficiários",
                        value = uiState.dashboardStats?.totalBeneficiaries?.toString() ?: "0",
                        subtitle = "Ativos",
                        icon = Icons.Default.Group,
                        iconTint = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f)
                    )

                    StatsCard(
                        title = "Entregas",
                        value = uiState.dashboardStats?.totalDeliveries?.toString() ?: "0",
                        subtitle = "Este mês",
                        icon = Icons.Default.LocalShipping,
                        iconTint = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Alerts Section
                if (uiState.dashboardStats?.hasAlerts() == true) {
                    Text(
                        text = "Alertas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Low Stock Alert
                    if ((uiState.dashboardStats.itemsInAlert ?: 0) > 0) {
                        AlertCard(
                            title = "Stock Baixo",
                            description = "${uiState.dashboardStats.itemsInAlert} artigos abaixo do mínimo",
                            icon = Icons.Default.Warning,
                            color = Color(0xFFFFA726),
                            onClick = onStockClick
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Expiring Items Alert
                    if ((uiState.dashboardStats.itemsExpiringSoon ?: 0) > 0) {
                        AlertCard(
                            title = "Validades Próximas",
                            description = "${uiState.dashboardStats.itemsExpiringSoon} artigos a expirar em breve",
                            icon = Icons.Default.EventBusy,
                            color = Color(0xFFF44336),
                            onClick = onStockClick
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Actions
                Text(
                    text = "Ações Rápidas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                QuickActionCard(
                    title = "Ver Stock",
                    description = "Gerir lotes e movimentos",
                    icon = Icons.Default.Inventory,
                    onClick = onStockClick
                )

                Spacer(modifier = Modifier.height(8.dp))

                QuickActionCard(
                    title = "Ver Doações",
                    description = "Consultar histórico de doações",
                    icon = Icons.Default.CardGiftcard,
                    onClick = onDonationsClick
                )
            }
        }
    }
}

@Composable
private fun AlertCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminStartViewPreview() {
    LojaSocialTheme {
        AdminStartViewContent(
            uiState = AdminStartState(
                user = User(name = "Admin", email = "admin@test.com"),
                dashboardStats = DashboardStats(
                    totalItems = 15,
                    itemsInAlert = 3,
                    itemsExpiringSoon = 2,
                    totalDonations = 25,
                    totalBeneficiaries = 12,
                    totalDeliveries = 8
                ),
                isLoading = false,
                error = null
            )
        )
    }
}
