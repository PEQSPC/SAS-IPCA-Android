package com.example.lojasocial.ui.admin.donations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.AppConstants
import com.example.lojasocial.models.Donation
import com.example.lojasocial.ui.theme.LojaSocialTheme
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DonationsListView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vm: DonationsListViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) { vm.fetch() }

    DonationsListViewContent(
        navController = navController,
        modifier = modifier,
        uiState = uiState,
        onSearchChange = vm::setSearch,
        onCreateClick = {
            navController.navigate(AppConstants.createDonation) { launchSingleTop = true }
        },
        onOpenClick = { donation ->
            val id = donation.docId?.trim().orEmpty()
            if (id.isNotBlank()) {
                navController.navigate(
                    AppConstants.donationDetail.replace("{docId}", id)
                ) { launchSingleTop = true }
            }
        }
    )
}

@Composable
fun DonationsListViewContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    uiState: DonationsListState,
    onSearchChange: (String) -> Unit = {},
    onCreateClick: () -> Unit = {},
    onOpenClick: (Donation) -> Unit = {}
) {
    val filtered = remember(uiState.items, uiState.search) {
        val q = uiState.search.trim().lowercase()

        uiState.items.filter { donation ->
            q.isBlank() ||
                (donation.donorName ?: "").lowercase().contains(q) ||
                (donation.notes ?: "").lowercase().contains(q)
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
                text = "Doações",
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
                    placeholder = { Text("Pesquisar por doador...") },
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
                        text = "Sem doações. Carrega em Nova para registar.",
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
                            DonationRow(
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
private fun DonationRow(
    item: Donation,
    onOpen: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val dateText = item.date?.toDate()?.let { dateFormatter.format(it) } ?: "—"

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
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = Color(0xFF1B5E20),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = item.donorName?.ifBlank { "—" } ?: "—",
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
                if (!item.notes.isNullOrBlank()) {
                    Text(
                        text = "•",
                        color = Color.Black.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
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
}

@Preview(showBackground = true)
@Composable
fun DonationsListViewPreview() {
    LojaSocialTheme {
        DonationsListViewContent(
            navController = androidx.navigation.compose.rememberNavController(),
            uiState = DonationsListState(
                items = listOf(
                    Donation(
                        docId = "1",
                        donorName = "Empresa ABC",
                        date = Timestamp.now(),
                        notes = "Alimentos variados"
                    ),
                    Donation(
                        docId = "2",
                        donorName = "João Silva",
                        date = Timestamp.now(),
                        notes = null
                    )
                ),
                isLoading = false
            )
        )
    }
}
