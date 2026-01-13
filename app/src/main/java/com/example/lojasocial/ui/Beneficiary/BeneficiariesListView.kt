package com.example.lojasocial.ui.beneficiary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.AppConstants
import com.example.lojasocial.models.Beneficiary
import com.example.lojasocial.ui.admin.components.BeneficiaryStatusBadge
import com.example.lojasocial.ui.theme.LojaSocialTheme
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun BeneficiariesListView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vm: BeneficiariesListViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) { vm.fetch() }

    BeneficiariesListViewContent(
        modifier = modifier,
        uiState = uiState,
        onSearchChange = vm::setSearch,
        onStatusFilterChange = vm::setStatusFilter,
        onCreateClick = { navController.navigate(AppConstants.createBeneficiary) },
        onItemClick = { b ->
            val id = b.docId ?: return@BeneficiariesListViewContent
            navController.navigate(AppConstants.beneficiaryDetail.replace("{docId}", id))
        }
    )
}

@Composable
fun BeneficiariesListViewContent(
    modifier: Modifier = Modifier,
    uiState: BeneficiariesListState,
    onSearchChange: (String) -> Unit = {},
    onStatusFilterChange: (String?) -> Unit = {},
    onCreateClick: () -> Unit = {},
    onItemClick: (Beneficiary) -> Unit = {}
) {
    val query = uiState.search.trim()
    val filtered = remember(uiState.items, query, uiState.statusFilter) {
        uiState.items.filter { b ->
            val matchesSearch = if (query.isBlank()) true
            else {
                val nome = b.nome.orEmpty()
                val numeroAluno = b.numeroAluno.orEmpty()
                nome.contains(query, ignoreCase = true) ||
                numeroAluno.contains(query, ignoreCase = true)
            }

            val matchesStatus = uiState.statusFilter == null ||
                b.status?.uppercase() == uiState.statusFilter?.uppercase()

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
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            Text(
                text = "Beneficiários",
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.search,
                    onValueChange = onSearchChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Pesquisar por nome...") },
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
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0B1220),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Criar")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // STATUS FILTER
            StatusFilterRow(
                currentFilter = uiState.statusFilter,
                onFilterChange = onStatusFilterChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2E7D32))
                    }
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }

                filtered.isEmpty() -> {
                    Text(
                        text = "Sem beneficiários.",
                        color = Color.Black,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(filtered) { item ->
                            BeneficiaryCard(
                                beneficiary = item,
                                onClick = { onItemClick(item) }
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
private fun BeneficiaryCard(
    beneficiary: Beneficiary,
    onClick: () -> Unit
) {
    val name = beneficiary.nome ?: "(Sem nome)"
    val initials = name.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.take(1).uppercase() }
        .ifBlank { "?" }

    // Calculate relative time for last delivery
    val lastDeliveryText = beneficiary.lastDeliveryDate?.let { timestamp ->
        val now = System.currentTimeMillis()
        val deliveryTime = timestamp.toDate().time
        val diffMillis = now - deliveryTime
        val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

        when {
            diffDays == 0 -> "Hoje"
            diffDays == 1 -> "Há 1 dia"
            diffDays < 7 -> "Há $diffDays dias"
            diffDays < 30 -> "Há ${diffDays / 7} semanas"
            else -> {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dateFormat.format(timestamp.toDate())
            }
        }
    } ?: "Sem entregas"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 10.dp),
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
            Text(text = initials, color = Color(0xFF1B5E20))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = name,
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                BeneficiaryStatusBadge(status = beneficiary.status)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!beneficiary.numeroAluno.isNullOrBlank()) {
                    Text(
                        text = beneficiary.numeroAluno!!,
                        color = Color.Black.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "•",
                        color = Color.Black.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = "Última entrega: $lastDeliveryText",
                    color = Color.Black.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun StatusFilterRow(
    currentFilter: String?,
    onFilterChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentFilter == null,
            onClick = { onFilterChange(null) },
            label = { Text("Todos") }
        )
        FilterChip(
            selected = currentFilter == "ACTIVE",
            onClick = { onFilterChange("ACTIVE") },
            label = { Text("Ativo") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFF66BB6A).copy(alpha = 0.2f),
                selectedLabelColor = Color(0xFF2E7D32)
            )
        )
        FilterChip(
            selected = currentFilter == "PENDING",
            onClick = { onFilterChange("PENDING") },
            label = { Text("Pendente") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFFFFA726).copy(alpha = 0.2f),
                selectedLabelColor = Color(0xFFF57C00)
            )
        )
        FilterChip(
            selected = currentFilter == "INACTIVE",
            onClick = { onFilterChange("INACTIVE") },
            label = { Text("Inativo") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFFBDBDBD).copy(alpha = 0.2f),
                selectedLabelColor = Color(0xFF616161)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BeneficiariesListViewPreview() {
    LojaSocialTheme {
        BeneficiariesListViewContent(
            uiState = BeneficiariesListState(
                items = listOf(
                    Beneficiary(
                        docId = "1",
                        nome = "Maria Alves",
                        numeroAluno = "20001",
                        status = "ACTIVE",
                        lastDeliveryDate = Timestamp(Date(System.currentTimeMillis() - 86400000 * 3)) // 3 days ago
                    ),
                    Beneficiary(
                        docId = "2",
                        nome = "Antonio Ferreira",
                        numeroAluno = "20002",
                        status = "PENDING",
                        lastDeliveryDate = null
                    ),
                    Beneficiary(
                        docId = "3",
                        nome = "João Silva",
                        numeroAluno = "20003",
                        status = "INACTIVE",
                        lastDeliveryDate = Timestamp(Date(System.currentTimeMillis() - 86400000 * 15)) // 15 days ago
                    )
                )
            )
        )
    }
}