package com.example.lojasocial.ui.admin.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.lojasocial.models.StockMove
import com.example.lojasocial.ui.theme.LojaSocialTheme
import com.google.firebase.Timestamp
import java.util.Date

@Composable
fun StockMovesView(
    navController: NavController,
    viewModel: StockMovesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    StockMovesViewContent(
        state = state,
        onTypeSelect = { viewModel.selectType(it) },
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onRefresh = { viewModel.loadMoves() },
        onBackClick = { navController.popBackStack() },
        modifier = modifier
    )
}

@Composable
fun StockMovesViewContent(
    state: StockMovesState,
    onTypeSelect: (MoveType) -> Unit,
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

                Text(
                    text = "Histórico de Movimentos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
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

            // Type Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.selectedType == MoveType.ALL,
                    onClick = { onTypeSelect(MoveType.ALL) },
                    label = { Text("Todos") }
                )
                FilterChip(
                    selected = state.selectedType == MoveType.IN,
                    onClick = { onTypeSelect(MoveType.IN) },
                    label = { Text("Entradas") }
                )
                FilterChip(
                    selected = state.selectedType == MoveType.OUT,
                    onClick = { onTypeSelect(MoveType.OUT) },
                    label = { Text("Saídas") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Pesquisar por produto ou lote") },
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
            else if (state.filteredMoves.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum movimento encontrado",
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                }
            }

            // Moves List
            else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredMoves) { moveWithName ->
                        MoveCard(moveWithName = moveWithName)
                    }
                }
            }
        }
    }
}

@Composable
private fun MoveCard(
    moveWithName: MoveWithItemName,
    modifier: Modifier = Modifier
) {
    val move = moveWithName.move
    val isIn = move.type == "IN"
    val iconColor = if (isIn) Color(0xFF2E7D32) else Color(0xFFF44336)
    val icon = if (isIn) Icons.Default.ArrowCircleUp else Icons.Default.ArrowCircleDown

    // Format timestamp to readable date
    val dateString = move.createdAt?.let { timestamp ->
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.US)
        dateFormat.format(timestamp.toDate())
    } ?: "Data desconhecida"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
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
            // Icon
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = if (isIn) "Entrada" else "Saída",
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = moveWithName.itemName ?: "Produto desconhecido",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lote ${move.lotNumber ?: "N/A"}",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )

                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.3f)
                    )

                    Text(
                        text = dateString,
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Quantity
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isIn) "+${move.quantity}" else "-${move.quantity}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                )
                Text(
                    text = if (isIn) "Entrada" else "Saída",
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StockMovesViewPreview() {
    LojaSocialTheme {
        StockMovesViewContent(
            state = StockMovesState(
                filteredMoves = listOf(
                    MoveWithItemName(
                        move = StockMove(
                            itemId = "1",
                            lotNumber = "LOT-001",
                            type = "IN",
                            quantity = 100,
                            createdAt = Timestamp(Date())
                        ),
                        itemName = "Arroz"
                    ),
                    MoveWithItemName(
                        move = StockMove(
                            itemId = "2",
                            lotNumber = "LOT-002",
                            type = "OUT",
                            quantity = 25,
                            createdAt = Timestamp(Date())
                        ),
                        itemName = "Leite"
                    ),
                    MoveWithItemName(
                        move = StockMove(
                            itemId = "1",
                            lotNumber = "LOT-003",
                            type = "IN",
                            quantity = 50,
                            createdAt = Timestamp(Date())
                        ),
                        itemName = "Arroz"
                    )
                )
            ),
            onTypeSelect = {},
            onSearchQueryChange = {},
            onRefresh = {},
            onBackClick = {}
        )
    }
}
