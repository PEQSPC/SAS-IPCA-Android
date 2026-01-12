package com.example.lojasocial.ui.admin.stock

import androidx.compose.foundation.background
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
import com.example.lojasocial.models.Item
import com.example.lojasocial.models.StockLot
import com.example.lojasocial.ui.admin.components.ExpiryStatusBadge
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun StockLotsView(
    navController: NavController,
    viewModel: StockLotsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    StockLotsViewContent(
        state = state,
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onRefresh = { viewModel.loadItemAndLots() },
        onBackClick = { navController.popBackStack() },
        modifier = modifier
    )
}

@Composable
fun StockLotsViewContent(
    state: StockLotsState,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onBackClick: () -> Unit,
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
            // Header with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.item?.name ?: "Carregando...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (state.item != null) {
                        Text(
                            text = "SKU: ${state.item.sku ?: "N/A"}",
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

            Spacer(modifier = Modifier.height(16.dp))

            // Stock Summary Card
            if (state.item != null) {
                StockSummaryCard(item = state.item)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Pesquisar por lote ou doador") },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Section Title
            Text(
                text = "Lotes (${state.filteredLots.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                            text = state.error,
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
            else if (state.filteredLots.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum lote encontrado",
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                }
            }

            // Lots List
            else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredLots) { lotWithDonor ->
                        LotCard(lotWithDonor = lotWithDonor)
                    }
                }
            }
        }
    }
}

@Composable
private fun StockSummaryCard(
    item: Item,
    modifier: Modifier = Modifier
) {
    val current = item.stockCurrent ?: 0
    val min = item.minStock ?: 0
    val isLowStock = current < min

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Stock Atual",
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
                Text(
                    text = "$current ${item.unit ?: ""}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLowStock) Color(0xFFF44336) else Color(0xFF2E7D32)
                )
            }

            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(50.dp),
                color = Color.Black.copy(alpha = 0.1f)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Stock Mínimo",
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
                Text(
                    text = "$min ${item.unit ?: ""}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun LotCard(
    lotWithDonor: LotWithDonor,
    modifier: Modifier = Modifier
) {
    val lot = lotWithDonor.lot
    val quantity = lot.quantity
    val remainingQty = lot.remainingQty
    val percentage = if (quantity > 0) {
        (remainingQty.toFloat() / quantity.toFloat() * 100).toInt()
    } else 0

    Card(
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
            // Header: Lot Number + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lote ${lot.lot ?: "N/A"}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    if (lotWithDonor.donorName != null) {
                        Text(
                            text = "Doador: ${lotWithDonor.donorName}",
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                ExpiryStatusBadge(daysUntilExpiry = lotWithDonor.daysUntilExpiry)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quantity Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Quantidade",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$remainingQty / $quantity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Restante",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$percentage%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            percentage < 20 -> Color(0xFFF44336)
                            percentage < 50 -> Color(0xFFFFA726)
                            else -> Color(0xFF2E7D32)
                        }
                    )
                }
            }

            // Progress Bar
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = percentage / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = when {
                    percentage < 20 -> Color(0xFFF44336)
                    percentage < 50 -> Color(0xFFFFA726)
                    else -> Color(0xFF2E7D32)
                },
                trackColor = Color.Black.copy(alpha = 0.1f)
            )

            // Expiry Date
            if (lot.expiryDate != null) {
                val expiryDateFormatted = lot.expiryDate?.let { timestamp ->
                    val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.US)
                    dateFormat.format(timestamp.toDate())
                } ?: "N/A"

                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Validade: $expiryDateFormatted",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                    if (lotWithDonor.daysUntilExpiry != null && lotWithDonor.daysUntilExpiry >= 0) {
                        Text(
                            text = " (${lotWithDonor.daysUntilExpiry} dias)",
                            fontSize = 12.sp,
                            color = when (lotWithDonor.status) {
                                LotStatus.EXPIRED -> Color(0xFFF44336)
                                LotStatus.EXPIRING_VERY_SOON -> Color(0xFFF44336)
                                LotStatus.EXPIRING_SOON -> Color(0xFFFFA726)
                                else -> Color.Black.copy(alpha = 0.6f)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StockLotsViewPreview() {
    LojaSocialTheme {
        StockLotsViewContent(
            state = StockLotsState(
                item = Item(
                    docId = "1",
                    name = "Arroz",
                    sku = "ARR001",
                    stockCurrent = 50,
                    minStock = 20,
                    unit = "kg"
                ),
                filteredLots = listOf(
                    LotWithDonor(
                        lot = StockLot(
                            lot = "LOT-001",
                            quantity = 100,
                            remainingQty = 75,
                            expiryDate = com.google.firebase.Timestamp(java.util.Date())
                        ),
                        donorName = "João Silva",
                        daysUntilExpiry = 45,
                        status = LotStatus.OK
                    ),
                    LotWithDonor(
                        lot = StockLot(
                            lot = "LOT-002",
                            quantity = 50,
                            remainingQty = 10,
                            expiryDate = com.google.firebase.Timestamp(java.util.Date())
                        ),
                        donorName = "Maria Santos",
                        daysUntilExpiry = 15,
                        status = LotStatus.EXPIRING_SOON
                    )
                )
            ),
            onSearchQueryChange = {},
            onRefresh = {},
            onBackClick = {}
        )
    }
}
