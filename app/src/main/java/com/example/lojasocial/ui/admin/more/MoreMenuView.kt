package com.example.lojasocial.ui.admin.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.AppConstants
import com.example.lojasocial.core.auth.AuthStateHolder
import com.example.lojasocial.ui.theme.LojaSocialTheme

data class QuickAccessItem(
    val icon: ImageVector,
    val title: String,
    val route: String
)

@Composable
fun MoreMenuView(
    navController: NavController,
    authStateHolder: AuthStateHolder? = null,
    modifier: Modifier = Modifier
) {
    val quickAccessItems = listOf(
        QuickAccessItem(Icons.Default.Handshake, "Doadores", AppConstants.donors),
        QuickAccessItem(Icons.Default.CalendarMonth, "Agendamentos", AppConstants.agendas),
        QuickAccessItem(Icons.Default.LocalShipping, "Entregas", AppConstants.deliveries),
        QuickAccessItem(Icons.Default.Category, "Categorias", AppConstants.families),
        QuickAccessItem(Icons.Default.Person, "Perfil", AppConstants.profile)
    )

    MoreMenuViewContent(
        quickAccessItems = quickAccessItems,
        onQuickAccessClick = { item ->
            navController.navigate(item.route) { launchSingleTop = true }
        },
        onStockClick = {
            navController.navigate(AppConstants.stock) { launchSingleTop = true }
        },
        onReportsClick = {
            navController.navigate(AppConstants.reports) { launchSingleTop = true }
        },
        onDonationsClick = {
            navController.navigate(AppConstants.donations) { launchSingleTop = true }
        },
        onLogoutClick = {
            authStateHolder?.signOut()
        },
        modifier = modifier
    )
}

@Composable
fun MoreMenuViewContent(
    quickAccessItems: List<QuickAccessItem>,
    onQuickAccessClick: (QuickAccessItem) -> Unit,
    onStockClick: () -> Unit,
    onReportsClick: () -> Unit,
    onDonationsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
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
            Text(
                text = "Mais",
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF2E7D32)
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Sair",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ACESSO RÁPIDO
            Text(
                text = "Acesso rápido",
                color = Color.Black.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )

            // Grid 2x3 de cards
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primeira linha: 2 cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    quickAccessItems.take(2).forEach { item ->
                        QuickAccessCard(
                            item = item,
                            onClick = { onQuickAccessClick(item) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Segunda linha: 2 cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    quickAccessItems.drop(2).take(2).forEach { item ->
                        QuickAccessCard(
                            item = item,
                            onClick = { onQuickAccessClick(item) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Terceira linha: 1 card (Perfil)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    quickAccessItems.drop(4).take(1).forEach { item ->
                        QuickAccessCard(
                            item = item,
                            onClick = { onQuickAccessClick(item) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Spacer para manter grid alinhado
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // OUTRAS OPÇÕES (lista)
            Text(
                text = "Outras opções",
                color = Color.Black.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MenuOptionCard(
                    icon = Icons.Default.Inventory,
                    title = "Gestão de Stock",
                    subtitle = "Lotes, movimentos e alertas",
                    onClick = onStockClick
                )

                MenuOptionCard(
                    icon = Icons.Default.Assessment,
                    title = "Relatórios",
                    subtitle = "Exportar dados",
                    onClick = onReportsClick
                )

                MenuOptionCard(
                    icon = Icons.Default.CardGiftcard,
                    title = "Doações",
                    subtitle = "Ver todas as doações",
                    onClick = onDonationsClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickAccessCard(
    item: QuickAccessItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun MenuOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
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
                contentDescription = title,
                tint = Color.Black.copy(alpha = 0.7f),
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
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ir para $title",
                tint = Color.Black.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoreMenuViewPreview() {
    LojaSocialTheme {
        MoreMenuViewContent(
            quickAccessItems = listOf(
                QuickAccessItem(Icons.Default.Handshake, "Doadores", "donors"),
                QuickAccessItem(Icons.Default.CalendarMonth, "Agendamentos", "agendas"),
                QuickAccessItem(Icons.Default.LocalShipping, "Entregas", "deliveries"),
                QuickAccessItem(Icons.Default.Category, "Categorias", "families"),
                QuickAccessItem(Icons.Default.Person, "Perfil", "profile")
            ),
            onQuickAccessClick = {},
            onStockClick = {},
            onReportsClick = {},
            onDonationsClick = {},
            onLogoutClick = {}
        )
    }
}
