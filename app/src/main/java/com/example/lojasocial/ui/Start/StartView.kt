package com.example.lojasocial.ui.Start

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.lojasocial.AppConstants
import com.example.lojasocial.R
import com.example.lojasocial.models.Product
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun StartView(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val productViewModel: StartProductViewModel = viewModel()
    val uiState by productViewModel.uiState

    LaunchedEffect(Unit) {
        productViewModel.loadProduct(productId = 1)
    }

    StartViewContent(
        modifier = modifier,
        uiState = uiState,
        onLoginClick = onLoginClick,
        onRegisterClick = onRegisterClick
    )
}

@Composable
fun StartViewContent(
    modifier: Modifier = Modifier,
    uiState: ProductState,
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // Botão Login
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Login")
        }

        // Botão Registo
        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Criar Conta")
        }

        // Conteúdo central
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // LOGO
            Image(
                painter = painterResource(id = R.drawable.ipca1),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(120.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "A carregar produto...",
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }

                uiState.product != null -> {
                    ProductCard(uiState.product)
                }

                else -> {
                    Text(
                        text = "Nenhum produto disponível.",
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Rodapé com logos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 10.dp, bottom = 40.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ipca),
                contentDescription = null,
                modifier = Modifier.height(30.dp),
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(id = R.drawable.est),
                contentDescription = null,
                modifier = Modifier.height(80.dp),
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(id = R.drawable.sassocial),
                contentDescription = null,
                modifier = Modifier.height(80.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Produto em destaque",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .background(Color.White),
                contentScale = ContentScale.Fit,
                placeholder = painterResource(R.drawable.ic_placeholder),
                error = painterResource(R.drawable.ic_error)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.title ?: "",
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Text(
                text = "Categoria: ${product.category ?: "-"}",
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )

            Text(
                text = "Preço: ${product.price ?: 0.0} €",
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            product.description?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartViewPreview() {
    LojaSocialTheme {
        StartViewContent(
            uiState = ProductState(
                product = Product(
                    title = "Produto Exemplo",
                    category = "Categoria X",
                    description = "Descrição de teste do produto em destaque.",
                    price = 19.99,
                    thumbnail = "https://dummyjson.com/image/i/products/1/thumbnail.jpg"
                ),
                isLoading = false,
                error = null
            )
        )
    }
}
