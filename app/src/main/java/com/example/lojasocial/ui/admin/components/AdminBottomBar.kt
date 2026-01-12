package com.example.lojasocial.ui.admin.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.lojasocial.AppConstants

enum class AdminTab(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    HOME(AppConstants.adminHome, Icons.Default.Home, "Início"),
    BENEFICIARIES(AppConstants.beneficiaries, Icons.Default.Group, "Benef."),
    DONATIONS(AppConstants.donations, Icons.Default.CardGiftcard, "Doações"),
    PRODUCTS(AppConstants.products, Icons.Default.Inventory2, "Artigos"),
    DELIVERIES(AppConstants.deliveries, Icons.Default.LocalShipping, "Entregas"),
    MORE(AppConstants.more, Icons.Default.MoreHoriz, "Mais")
}

@Composable
fun AdminBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        modifier = modifier.height(80.dp),
        containerColor = Color(0xFFDFF3E3),
        tonalElevation = 6.dp,
        windowInsets = WindowInsets(0)
    ) {
        AdminTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = {
                    navController.navigate(tab.route) {
                        // Pop up to admin home to avoid deep stacks
                        popUpTo(AppConstants.adminHome) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alwaysShowLabel = true,
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = {
                    Text(
                        text = tab.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}
