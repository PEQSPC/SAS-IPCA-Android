package com.example.lojasocial.ui.admin.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.AppConstants
import com.example.lojasocial.models.Item
import com.example.lojasocial.ui.admin.components.StockStatusBadge
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun StockOverviewView(
    navController: NavController,
    viewModel: StockOverviewViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    StockOverviewViewContent(
        state = state,
        onToggleLowStockFilter = { viewModel.toggleLowStockFilter() },
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onItemClick = { itemId ->
            navController.navigate(AppConstants.stockLots.replace("{itemId}", itemId))
        },
        onRefresh = { viewModel.loadItems() },
        modifier = modifier
    )
}

@Composable
fun StockOverviewViewContent(
    state: StockOverviewState,
    onToggleLowStockFilter: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onItemClick: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gestão de Stock",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Atualizar",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Pesquisar por nome, SKU ou EAN") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Pesquisar")
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Low Stock Filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleLowStockFilter() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.showOnlyLowStock,
                    onCheckedChange = { onToggleLowStockFilter() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Apenas stock baixo",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Loading State
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2E7D32))
                }
            }

            // Error State
            else if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Erro: ${state.error}",
                            color = Color.Red,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRefresh) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }

            // Empty State
            else if (state.filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.showOnlyLowStock) "Nenhum artigo com stock baixo" else "Nenhum artigo encontrado",
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                }
            }

            // Items List
            else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredItems) { itemInfo ->
                        StockItemCard(
                            itemInfo = itemInfo,
                            onClick = { onItemClick(itemInfo.item.docId ?: "") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StockItemCard(
    itemInfo: ItemWithStockInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val item = itemInfo.item
    val current = item.stockCurrent ?: 0
    val min = item.minStock ?: 0
    val isLowStock = current < min

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Name + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name ?: "Sem nome",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "SKU: ${item.sku ?: "N/A"}",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                StockStatusBadge(
                    currentStock = current,
                    minStock = min
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stock Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Stock atual",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$current ${item.unit ?: ""}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLowStock) Color(0xFFF44336) else Color(0xFF2E7D32)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Stock mínimo",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$min ${item.unit ?: ""}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }

            // Additional Info
            if (item.localizacao != null || itemInfo.expiringInDays != null || itemInfo.activeLots > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.Black.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Location
                    if (item.localizacao != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = Color.Black.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.localizacao ?: "",
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Expiry Alert
                    if (itemInfo.expiringInDays != null && itemInfo.expiringInDays!! < 30) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFFA726),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Expira em ${itemInfo.expiringInDays} dias",
                                fontSize = 12.sp,
                                color = Color(0xFFFFA726),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Active Lots
                    if (itemInfo.activeLots > 0) {
                        Text(
                            text = "${itemInfo.activeLots} lotes ativos",
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StockOverviewViewPreview() {
    LojaSocialTheme {
        StockOverviewViewContent(
            state = StockOverviewState(
                filteredItems = listOf(
                    ItemWithStockInfo(
                        item = Item(
                            docId = "1",
                            sku = "ARR001",
                            name = "Arroz",
                            stockCurrent = 5,
                            minStock = 10,
                            unit = "kg",
                            localizacao = "Prateleira A1"
                        ),
                        activeLots = 2,
                        expiringInDays = 15
                    ),
                    ItemWithStockInfo(
                        item = Item(
                            docId = "2",
                            sku = "LEI002",
                            name = "Leite",
                            stockCurrent = 50,
                            minStock = 20,
                            unit = "L"
                        ),
                        activeLots = 3
                    )
                )
            ),
            onToggleLowStockFilter = {},
            onSearchQueryChange = {},
            onItemClick = {},
            onRefresh = {}
        )
    }
}
