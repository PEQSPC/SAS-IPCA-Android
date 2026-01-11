package com.example.lojasocial.ui.carts

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.navigation.NavController
import com.example.lojasocial.AppConstansts
import com.example.lojasocial.R
import com.example.lojasocial.models.Cart
import com.example.lojasocial.models.Profile
import com.example.lojasocial.ui.theme.LojaSocialTheme
import ipca.example.newapplicationfirebase.ui.carts.CartViewCell

// ---------- Composable "real" que usa NavController + ViewModel ----------

@Composable
fun CartsView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: CartsViewModel = viewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.fetchCarts()
    }

    CartsViewContent(
        modifier = modifier,
        uiState = uiState,
        onCartClick = { cart ->
            navController.navigate("products/${cart.docId}")
        },
        onAddClick = {
            viewModel.addCart()
        }
    )
}

// ---------- Composable "puro" para UI + Preview ----------

@Composable
fun CartsViewContent(
    modifier: Modifier = Modifier,
    uiState: CartsState,
    onCartClick: (Cart) -> Unit = {},
    onEditProfile : () -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Fundo (como no StartView / LoginView)
        Image(
            painter = painterResource(R.drawable.cores),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 80.dp),       // <<< desloca a lista mais abaixo
            verticalArrangement = Arrangement.Top, // <<< deixa tudo alinhado no topo
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Black
                        )
                    }

                    uiState.error != null -> {
                        Text(
                            text = uiState.error ?: "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(
                                items = uiState.carts
                            ) { _, item ->
                                CartViewCell(
                                    cart = item,
                                    onClick = { onCartClick(item) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ipca),
                    contentDescription = null,
                    modifier = Modifier.height(20.dp),
                    contentScale = ContentScale.Fit
                )

                Image(
                    painter = painterResource(id = R.drawable.sassocial),
                    contentDescription = null,
                    modifier = Modifier.height(30.dp),
                    contentScale = ContentScale.Fit
                )

                Image(
                    painter = painterResource(id = R.drawable.est),
                    contentDescription = null,
                    modifier = Modifier.height(30.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Botão Add no canto inferior direito
        Button(
            onClick = { onAddClick() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd),
            border = BorderStroke(1.dp, Color.White),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(AppConstansts.products)
        }

        
        Button(
            onClick = { onEditProfile() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart),
            border = BorderStroke(1.dp, Color.White),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(AppConstansts.loginRoute)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CartsViewPreview() {
    LojaSocialTheme {
        CartsViewContent(
            uiState = CartsState(
                carts = listOf(
                    Cart(name = "Stock 1"),
                    Cart(name = "Stock 2"),
                    Cart(name = "Stock 3")
                ),
                isLoading = false,
                error = null
            ),
            onCartClick = {},
            onAddClick = {}
        )
    }
}