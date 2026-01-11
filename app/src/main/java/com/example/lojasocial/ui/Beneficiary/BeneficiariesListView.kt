package com.example.lojasocial.ui.beneficiary

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.AppConstants
import com.example.lojasocial.R
import com.example.lojasocial.models.Beneficiary
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun BeneficiariesListView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vm: BeneficiariesListViewModel = viewModel()
    val uiState = vm.uiState.value

    LaunchedEffect(Unit) { vm.fetch() }

    BeneficiariesListViewContent(
        modifier = modifier,
        uiState = uiState,
        onSearchChange = vm::setSearch,
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
    onCreateClick: () -> Unit = {},
    onItemClick: (Beneficiary) -> Unit = {}
) {
    val query = uiState.search.trim()
    val filtered = remember(uiState.items, query) {
        if (query.isBlank()) uiState.items
        else uiState.items.filter {
            val nome = it.nome.orEmpty()
            nome.contains(query, ignoreCase = true)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.img),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 70.dp)
        ) {
            Text(
                text = "Beneficiários",
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
                            .padding(12.dp)
                    )
                }

                filtered.isEmpty() -> {
                    Text(
                        text = "Sem beneficiários.",
                        color = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(filtered) { item ->
                            BeneficiaryNameCell(
                                name = item.nome ?: "(Sem nome)",
                                onClick = { onItemClick(item) }
                            )
                            Divider(color = Color.White.copy(alpha = 0.22f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BeneficiaryNameCell(
    name: String,
    onClick: () -> Unit
) {
    val initials = name.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.take(1).uppercase() }
        .ifBlank { "?" }

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

        Text(
            text = name,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
                    Beneficiary(docId = "1", nome = "Maria Alves"),
                    Beneficiary(docId = "2", nome = "Antonio Ferreira")
                )
            )
        )
    }
}