package com.example.lojasocial.ui.admin.donors

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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
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
import com.example.lojasocial.models.Donor
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun DonorsListView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vm: DonorsListViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) { vm.fetch() }

    DonorsListViewContent(
        navController = navController,
        modifier = modifier,
        uiState = uiState,
        onSearchChange = vm::setSearch,
        onTypeFilterChange = vm::setTypeFilter,
        onCreateClick = {
            navController.navigate(AppConstants.createDonor) { launchSingleTop = true }
        },
        onOpenClick = { donor ->
            val id = donor.docId?.trim().orEmpty()
            if (id.isNotBlank()) {
                navController.navigate(
                    AppConstants.donorDetail.replace("{docId}", id)
                ) { launchSingleTop = true }
            }
        }
    )
}

@Composable
fun DonorsListViewContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    uiState: DonorsListState,
    onSearchChange: (String) -> Unit = {},
    onTypeFilterChange: (String) -> Unit = {},
    onCreateClick: () -> Unit = {},
    onOpenClick: (Donor) -> Unit = {}
) {
    val filtered = remember(
        uiState.items,
        uiState.search,
        uiState.typeFilter
    ) {
        val q = uiState.search.trim().lowercase()

        uiState.items.filter { donor ->
            val matchesSearch =
                q.isBlank() ||
                (donor.name ?: "").lowercase().contains(q) ||
                (donor.email ?: "").lowercase().contains(q) ||
                (donor.nif ?: "").lowercase().contains(q)

            val matchesType =
                uiState.typeFilter == "Todos" || (donor.type ?: "")
                    .equals(uiState.typeFilter, ignoreCase = true)

            matchesSearch && matchesType
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
                text = "Doadores",
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(14.dp))

            // PESQUISA + NOVO
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.search,
                    onValueChange = onSearchChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Pesquisar por nome, email ou NIF...") },
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
                    Text("Novo")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // FILTRO TIPO
            FilterDropdown(
                label = "Tipo",
                value = uiState.typeFilter,
                options = listOf("Todos", "COMPANY", "PRIVATE"),
                modifier = Modifier.fillMaxWidth(),
                onChange = onTypeFilterChange
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
                        text = "Sem doadores. Carrega em Novo para adicionar.",
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
                            DonorRow(
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
private fun DonorRow(
    item: Donor,
    onOpen: () -> Unit
) {
    val icon = when (item.type) {
        "COMPANY" -> Icons.Default.Business
        "PRIVATE" -> Icons.Default.Person
        else -> Icons.Default.Person
    }

    val typeLabel = when (item.type) {
        "COMPANY" -> "Empresa"
        "PRIVATE" -> "Particular"
        else -> "—"
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
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF1B5E20),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = item.name?.ifBlank { "—" } ?: "—",
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
                    text = typeLabel,
                    color = Color.Black.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
                if (!item.nif.isNullOrBlank()) {
                    Text(
                        text = "•",
                        color = Color.Black.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "NIF: ${item.nif}",
                        color = Color.Black.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
fun DonorsListViewPreview() {
    LojaSocialTheme {
        DonorsListViewContent(
            navController = androidx.navigation.compose.rememberNavController(),
            uiState = DonorsListState(
                items = listOf(
                    Donor(docId = "1", type = "COMPANY", name = "Empresa ABC", nif = "123456789"),
                    Donor(docId = "2", type = "PRIVATE", name = "João Silva", nif = "987654321")
                ),
                isLoading = false
            )
        )
    }
}
