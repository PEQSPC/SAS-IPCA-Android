package com.example.lojasocial.ui.family

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.AppConstansts
import com.example.lojasocial.R
import com.example.lojasocial.models.Family
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun FamiliesListView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vm: FamiliesListViewModel = viewModel()
    val uiState = vm.uiState.value

    LaunchedEffect(Unit) { vm.fetch() }

    FamiliesListViewContent(
        navController = navController,
        modifier = modifier,
        uiState = uiState,
        onSearchChange = vm::setSearch,
        onCreateClick = {
            
            navController.navigate(AppConstansts.createFamily) { launchSingleTop = true }
        },
        onItemClick = { fam ->
            val id = fam.docId?.trim().orEmpty()
            if (id.isNotBlank()) {
                navController.navigate("families/detail/$id") { launchSingleTop = true }
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
    val green = Color(0xFF2E7D32)

    val filtered = remember(uiState.items, uiState.search) {
        val q = uiState.search.trim().lowercase()
        if (q.isBlank()) uiState.items
        else uiState.items.filter {
            (it.name ?: "").lowercase().contains(q) ||
                    (it.notes ?: "").lowercase().contains(q)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.img),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // -------- CONTEÚDO --------
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp, bottom = 12.dp)
            ) {

                Text(
                    text = "Famílias",
                    color = Color.White,
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
                            cursorColor = green,
                            focusedBorderColor = green,
                            unfocusedBorderColor = green.copy(alpha = 0.75f),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onCreateClick, // ✅ AQUI
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0B1220),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Criar")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when {
                    uiState.isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
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
                            color = Color.White,
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
                                Divider(color = Color.White.copy(alpha = 0.22f))
                            }
                        }
                    }
                }
            }

            // -------- BOTTOM BAR (SEM ENUM) --------
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .navigationBarsPadding(),
                containerColor = Color(0xFFDFF3E3),
                tonalElevation = 6.dp,
                windowInsets = WindowInsets(0)
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(AppConstansts.products) { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Produtos") },
                    label = { Text("Produtos", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(AppConstansts.beneficiaries) { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Group, contentDescription = "Benef.") },
                    label = { Text("Benef.", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(AppConstansts.createBeneficiary) { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Criar") },
                    label = { Text("Criar", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )

                // ✅ Estamos na lista das famílias
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.navigate(AppConstansts.families) { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.People, contentDescription = "Famílias") },
                    label = { Text("Famílias", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(AppConstansts.adminHome) { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Admin") },
                    label = { Text("Admin", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
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
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!item.notes.isNullOrBlank()) {
                Text(
                    text = item.notes!!,
                    color = Color.White.copy(alpha = 0.85f),
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
            navController = rememberNavController(),
            uiState = FamiliesListState(
                items = listOf(
                    Family(docId = "1", name = "Família A", notes = "Notas A"),
                    Family(docId = "2", name = "Família B", notes = "Notas B")
                )
            )
        )
    }
}