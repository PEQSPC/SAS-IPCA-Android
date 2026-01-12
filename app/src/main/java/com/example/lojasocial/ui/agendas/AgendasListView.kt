package com.example.lojasocial.ui.agendas

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.AppConstants
import com.example.lojasocial.models.Agenda
import com.example.lojasocial.ui.theme.LojaSocialTheme
import java.util.Calendar

// ------------ STATE ------------

data class AgendasListState(
    val items: List<Agenda> = emptyList(),
    val search: String = "",
    val typeFilter: String = "Todos",
    val statusFilter: String = "Todos",
    val dateFrom: String = "",   // yyyy-MM-dd
    val dateTo: String = "",     // yyyy-MM-dd
    val isLoading: Boolean = false,
    val error: String? = null
)

// ------------ VIEW ------------

@Composable
fun AgendasListView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vm: AgendasListViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) { vm.fetch() }

    AgendasListViewContent(
        navController = navController,
        modifier = modifier,
        uiState = uiState,
        onSearchChange = vm::setSearch,
        onTypeFilterChange = vm::setTypeFilter,
        onStatusFilterChange = vm::setStatusFilter,
        onDateFromChange = vm::setDateFrom,
        onDateToChange = vm::setDateTo,
        onCreateClick = {
            navController.navigate(AppConstants.agendasCreate) { launchSingleTop = true }
        },
        onOpenClick = { agenda ->
            val id = agenda.docId?.trim().orEmpty()
            if (id.isNotBlank()) {
                navController.navigate(
                    AppConstants.agendasDetail.replace("{docId}", id)
                ) { launchSingleTop = true }
            }
        }
    )
}

@Composable
fun AgendasListViewContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    uiState: AgendasListState,
    onSearchChange: (String) -> Unit = {},
    onTypeFilterChange: (String) -> Unit = {},
    onStatusFilterChange: (String) -> Unit = {},
    onDateFromChange: (String) -> Unit = {},
    onDateToChange: (String) -> Unit = {},
    onCreateClick: () -> Unit = {},
    onOpenClick: (Agenda) -> Unit = {}
) {
    val context = LocalContext.current

    // ✅ Filtrar (mantém os teus filtros, mas a lista mostra só Nome + Data)
    val filtered = remember(
        uiState.items,
        uiState.search,
        uiState.typeFilter,
        uiState.statusFilter,
        uiState.dateFrom,
        uiState.dateTo
    ) {
        val q = uiState.search.trim().lowercase()

        fun inRange(date: String?): Boolean {
            val d = (date ?: "").trim()
            if (d.isBlank()) return true
            if (uiState.dateFrom.isNotBlank() && d < uiState.dateFrom.trim()) return false
            if (uiState.dateTo.isNotBlank() && d > uiState.dateTo.trim()) return false
            return true
        }

        uiState.items.filter { a ->
            val matchesSearch =
                q.isBlank() || (a.entity ?: "").lowercase().contains(q)

            val matchesType =
                uiState.typeFilter == "Todos" || (a.type ?: "")
                    .equals(uiState.typeFilter, ignoreCase = true)

            val matchesStatus =
                uiState.statusFilter == "Todos" || (a.status ?: "")
                    .equals(uiState.statusFilter, ignoreCase = true)

            matchesSearch && matchesType && matchesStatus && inRange(a.date)
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
                text = "Agendas",
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
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0B1220),
                        contentColor = Color.White
                    )
                ) {
                    Text("+ Novo")
                }
            }

                Spacer(modifier = Modifier.height(12.dp))

                // FILTROS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterDropdown(
                        label = "Tipo",
                        value = uiState.typeFilter,
                        options = listOf("Todos", "Entrega", "Recolha", "Doação"),
                        modifier = Modifier.weight(1f),
                        onChange = onTypeFilterChange
                    )

                    FilterDropdown(
                        label = "Estado",
                        value = uiState.statusFilter,
                        options = listOf("Todos", "Planeado", "Confirmado"),
                        modifier = Modifier.weight(1f),
                        onChange = onStatusFilterChange
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DateField(
                        label = "Data de",
                        value = uiState.dateFrom,
                        modifier = Modifier.weight(1f),
                        onPick = { picked -> onDateFromChange(picked) }
                    )
                    DateField(
                        label = "Data até",
                        value = uiState.dateTo,
                        modifier = Modifier.weight(1f),
                        onPick = { picked -> onDateToChange(picked) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

            // Cabeçalho (só Nome + Data)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderCell("Nome", 0.65f)
                HeaderCell("Data", 0.35f, alignEnd = true)
            }

            Divider(color = Color.Black.copy(alpha = 0.12f))

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
                        text = "Sem agendas. Carrega em + Novo para adicionar.",
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
                            AgendaRow(
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
private fun HeaderCell(
    text: String,
    weight: Float,
    alignEnd: Boolean = false
) {
    Text(
        text = text,
        color = Color.Black.copy(alpha = 0.6f),
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = if (alignEnd) TextAlign.End else TextAlign.Start
    )
}

@Composable
private fun AgendaRow(
    item: Agenda,
    onOpen: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .padding(horizontal = 6.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.entity?.ifBlank { "—" } ?: "—",
            modifier = Modifier.weight(0.65f),
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = item.date?.ifBlank { "—" } ?: "—",
            modifier = Modifier.weight(0.35f),
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End
        )
    }
}

// ------------ FILTROS ------------

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
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
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

@Composable
private fun DateField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onPick: (String) -> Unit
) {
    val context = LocalContext.current
    val cal = remember { Calendar.getInstance() }

    fun openPicker() {
        DatePickerDialog(
            context,
            { _, y, m, d ->
                val mm = (m + 1).toString().padStart(2, '0')
                val dd = d.toString().padStart(2, '0')
                onPick("$y-$mm-$dd")
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        enabled = false,
        modifier = modifier.noRippleClickable { openPicker() },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        trailingIcon = {
            IconButton(onClick = { openPicker() }) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Escolher data")
            }
        }
    )
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
fun AgendasListViewPreview() {
    LojaSocialTheme {
        AgendasListViewContent(
            navController = androidx.navigation.compose.rememberNavController(),
            uiState = AgendasListState(
                items = listOf(
                    Agenda(docId = "1", date = "2025-11-15", entity = "Maria Alves"),
                    Agenda(docId = "2", date = "2025-11-16", entity = "Mercado X"),
                    Agenda(docId = "3", date = "2025-11-27", entity = "João Silva")
                ),
                isLoading = false
            )
        )
    }
}