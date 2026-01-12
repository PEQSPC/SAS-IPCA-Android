package com.example.lojasocial.ui.family

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.lojasocial.models.Family
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun FamiliesListView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vm: FamiliesListViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) { vm.fetch() }

    FamiliesListViewContent(
        navController = navController,
        modifier = modifier,
        uiState = uiState,
        onSearchChange = vm::setSearch,
        onCreateClick = {
            
            navController.navigate(AppConstants.createFamily) { launchSingleTop = true }
        },
        onItemClick = { fam ->
            val id = fam.docId?.trim().orEmpty()
            if (id.isNotBlank()) {
                navController.navigate(
                    AppConstants.familyDetail.replace("{docId}", id)
                ) { launchSingleTop = true }
            }
        }
    )
}

@Composable
fun FamiliesListViewContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    uiState: FamiliesListState,
    onSearchChange: (String) -> Unit = {},
    onCreateClick: () -> Unit = {},
    onItemClick: (Family) -> Unit = {}
) {
    val filtered = remember(uiState.items, uiState.search) {
        val q = uiState.search.trim().lowercase()
        if (q.isBlank()) uiState.items
        else uiState.items.filter {
            (it.name ?: "").lowercase().contains(q) ||
                    (it.notes ?: "").lowercase().contains(q)
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
                text = "Famílias",
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
                    placeholder = { Text("Pesquisar por nome/notas...") },
                    singleLine = true,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
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
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
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
                            .padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }

                filtered.isEmpty() -> {
                    Text(
                        text = "Sem famílias. Carrega em Criar para adicionar.",
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp),
                        textAlign = TextAlign.Center
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(filtered) { item ->
                            FamilyCell(item = item, onClick = { onItemClick(item) })
                            Divider(color = Color.Black.copy(alpha = 0.12f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FamilyCell(
    item: Family,
    onClick: () -> Unit
) {
    val name = item.name ?: "(Sem nome)"
    val initials = name.trim().take(1).uppercase().ifBlank { "?" }

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
            Text(
                text = name,
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!item.notes.isNullOrBlank()) {
                Text(
                    text = item.notes!!,
                    color = Color.Black.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FamiliesListViewPreview() {
    LojaSocialTheme {
        FamiliesListViewContent(
            navController = androidx.navigation.compose.rememberNavController(),
            uiState = FamiliesListState(
                items = listOf(
                    Family(docId = "1", name = "Família A", notes = "Notas A"),
                    Family(docId = "2", name = "Família B", notes = "Notas B")
                )
            )
        )
    }
}