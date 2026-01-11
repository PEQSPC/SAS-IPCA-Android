package com.example.lojasocial.ui.Start

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.lojasocial.AppConstansts
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
    Box(modifier = modifier.fillMaxSize()) {

        //  Fundo (mantém igual)
        Image(
            painter = painterResource(R.drawable.img),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Box(modifier = Modifier.fillMaxSize()) {

            //  Conteúdo central
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //  LOGO EM CIMA DA API
                Image(
                    painter = painterResource(id = R.drawable.ipca1), // troca para o teu logo
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(120.dp)
                        .padding(bottom = 14.dp),
                    contentScale = ContentScale.Fit
                )

                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "A carregar produto...",
                            color = Color.White,
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
                        val product = uiState.product

                        Text(
                            text = "Produto em destaque",
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        AsyncImage(
                            model = product.thumbnail,
                            contentDescription = product.title,
                            modifier = Modifier
                                .height(120.dp)
                                .padding(bottom = 12.dp),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = product.title ?: "",
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 4.dp),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Categoria: ${product.category ?: "-"}",
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 2.dp),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Preço: ${product.price ?: 0.0} €",
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 2.dp),
                            textAlign = TextAlign.Center
                        )

                        if (!product.description.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.description!!,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    else -> {
                        Text(
                            text = "Nenhum produto disponível.",
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            //  Botões de cima (mantém igual)
            Button(
                onClick = { onLoginClick() },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd),
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(AppConstansts.loginRoute)
            }

            Button(
                onClick = { onRegisterClick() },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart),
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(AppConstansts.registerRoute)
            }


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
                    painter = painterResource(id = R.drawable.img_1),
                    contentDescription = null,
                    modifier = Modifier.height(80.dp),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(id = R.drawable.img_2),
                    contentDescription = null,
                    modifier = Modifier.height(80.dp),
                    contentScale = ContentScale.Fit
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
                    description = "Descrição de teste do produto em destaque na página inicial.",
                    price = 19.99,
                    thumbnail = "https://dummyjson.com/image/i/products/1/thumbnail.jpg"
                ),
                isLoading = false,
                error = null
            )
        )
    }
}