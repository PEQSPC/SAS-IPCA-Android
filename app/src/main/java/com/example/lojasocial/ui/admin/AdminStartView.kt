package com.example.lojasocial.ui.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lojasocial.AppConstants
import com.example.lojasocial.R
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun AdminStartView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: AdminStartViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNews(limit = 5)
    }

    AdminStartViewContent(
        modifier = modifier,
        uiState = uiState,
        onProductsClick = { navController.navigate(AppConstants.products) },
        onBeneficiariesClick = { navController.navigate(AppConstants.beneficiaries) },
        onCreateBeneficiaryClick = { navController.navigate(AppConstants.createBeneficiary) },
        onFamiliesClick = { navController.navigate(AppConstants.families) },
        onAgendasClick = { navController.navigate(AppConstants.agendas) },
        onProfileClick = { navController.navigate(AppConstants.profile) },
        onLogoutClick = { viewModel.logout() }
    )
}

@Composable
fun AdminStartViewContent(
    modifier: Modifier = Modifier,
    uiState: AdminStartState,
    onProductsClick: () -> Unit = {},
    onBeneficiariesClick: () -> Unit = {},
    onCreateBeneficiaryClick: () -> Unit = {},
    onFamiliesClick: () -> Unit = {},
    onAgendasClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(80.dp))

            Image(
                painter = painterResource(id = R.drawable.ipca1),
                contentDescription = "IPCA",
                modifier = Modifier.height(56.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Welcome message with user name
            uiState.user?.let { user ->
                Text(
                    text = "Olá, ${user.name}!",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Logout button
            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(Icons.Default.Logout, contentDescription = "Logout")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("A carregar notícias…")
                    }

                    uiState.error != null -> {
                        Text(
                            text = uiState.error,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }

                    uiState.news.isNotEmpty() -> {
                        NewsCard(uiState.news.first())
                    }

                    else -> {
                        Text("Nenhuma notícia disponível.")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NewsCard(news: News) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = news.body,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminStartViewPreview() {
    LojaSocialTheme {
        AdminStartViewContent(
            uiState = AdminStartState(
                user = null,
                news = listOf(
                    News(
                        title = "Notícia Exemplo",
                        body = "Esta é uma notícia de teste para demonstração."
                    )
                ),
                isLoading = false,
                error = null
            )
        )
    }
}
