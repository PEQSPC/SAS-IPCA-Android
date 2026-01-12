package com.example.lojasocial.ui.admin.deliveries

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.AppConstants
import com.example.lojasocial.models.Delivery
import com.example.lojasocial.ui.theme.LojaSocialTheme
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DeliveriesListView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vm: DeliveriesListViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) { vm.fetch() }

    DeliveriesListViewContent(
        navController = navController,
        modifier = modifier,
        uiState = uiState,
        onSearchChange = vm::setSearch,
        onStatusFilterChange = vm::setStatusFilter,
        onCreateClick = {
            navController.navigate(AppConstants.createDelivery) { launchSingleTop = true }
        },
        onOpenClick = { delivery ->
            val id = delivery.docId?.trim().orEmpty()
            if (id.isNotBlank()) {
                navController.navigate(
                    AppConstants.deliveryDetail.replace("{docId}", id)
                ) { launchSingleTop = true }
            }
        }
    )
}

@Composable
fun DeliveriesListViewContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    uiState: DeliveriesListState,
    onSearchChange: (String) -> Unit = {},
    onStatusFilterChange: (String) -> Unit = {},
    onCreateClick: () -> Unit = {},
    onOpenClick: (Delivery) -> Unit = {}
) {
    val filtered = remember(
        uiState.items,
        uiState.search,
        uiState.statusFilter
    ) {
        val q = uiState.search.trim().lowercase()

        uiState.items.filter { delivery ->
            val matchesSearch =
                q.isBlank() ||
                (delivery.beneficiaryName ?: "").lowercase().contains(q)

            val matchesStatus =
                uiState.statusFilter == "Todos" || (delivery.status ?: "")
                    .equals(uiState.statusFilter, ignoreCase = true)

            matchesSearch && matchesStatus
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            Text(
                text = "Entregas",
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(14.dp))

            // PESQUISA + NOVA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.search,
                    onValueChange = onSearchChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Pesquisar por beneficiário...") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onCreateClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0B1220),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nova")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // FILTRO ESTADO
            FilterDropdown(
                label = "Estado",
                value = uiState.statusFilter,
                options = listOf("Todos", "SCHEDULED", "DELIVERED"),
                modifier = Modifier.fillMaxWidth(),
                onChange = onStatusFilterChange
            )

            Spacer(modifier = Modifier.height(14.dp))

            when {
                uiState.isLoading -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2E7D32))
                    }
                }

                uiState.error != null -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = uiState.error ?: "",
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                filtered.isEmpty() -> {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Sem entregas. Carrega em Nova para planear.",
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        items(filtered) { item ->
                            DeliveryRow(
                                item = item,
                                onOpen = { onOpenClick(item) }
                            )
                            Divider(color = Color.Black.copy(alpha = 0.12f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeliveryRow(
    item: Delivery,
    onOpen: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val dateText = item.scheduledAt?.toDate()?.let { dateFormatter.format(it) } ?: "—"

    val statusLabel = when (item.status) {
        "SCHEDULED" -> "Planeada"
        "DELIVERED" -> "Entregue"
        else -> "—"
    }

    val statusColor = when (item.status) {
        "SCHEDULED" -> Color(0xFFFFA726) // Orange
        "DELIVERED" -> Color(0xFF66BB6A) // Green
        else -> Color.Gray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .padding(horizontal = 6.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFE6F4EA),
                shape = CircleShape
            ) {}
            Icon(
                imageVector = Icons.Default.LocalShipping,
                contentDescription = null,
                tint = Color(0xFF1B5E20),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = item.beneficiaryName?.ifBlank { "—" } ?: "—",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = dateText,
                    color = Color.Black.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "•",
                    color = Color.Black.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterDropdown(
    label: String,
    value: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir menu")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledContainerColor = Color.White,
                disabledBorderColor = Color.Black.copy(alpha = 0.38f),
                disabledLabelColor = Color.Black.copy(alpha = 0.38f),
                disabledTrailingIconColor = Color.Black
            )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 8.dp)
                .heightIn(min = 56.dp)
                .noRippleClickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onChange(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}

// clickable sem ripple (para overlays)
@Composable
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick() }
    )

@Preview(showBackground = true)
@Composable
fun DeliveriesListViewPreview() {
    LojaSocialTheme {
        DeliveriesListViewContent(
            navController = androidx.navigation.compose.rememberNavController(),
            uiState = DeliveriesListState(
                items = listOf(
                    Delivery(
                        docId = "1",
                        beneficiaryName = "Maria Silva",
                        status = "SCHEDULED",
                        scheduledAt = Timestamp.now()
                    ),
                    Delivery(
                        docId = "2",
                        beneficiaryName = "João Santos",
                        status = "DELIVERED",
                        scheduledAt = Timestamp.now()
                    )
                ),
                isLoading = false
            )
        )
    }
}
