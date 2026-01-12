package com.example.lojasocial.ui.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
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
import androidx.navigation.NavController
import com.example.lojasocial.R
import com.example.lojasocial.ui.BarcodeScanner.ScannerOverlay
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun ProductDetailView(
    navController: NavController,
    modifier: Modifier = Modifier,
    docId: String? = null,
    isReadOnly: Boolean = false
) {
    val vm: ProductDetailViewModel = viewModel()
    val uiState = vm.uiState.value

    //  normaliza docId (evita "{docId}" e strings vazias)
    val cleanDocId = docId?.trim()?.takeIf { it.isNotBlank() && it != "{docId}" }
    val isEditMode = cleanDocId != null

    //  quando muda de rota: se for editar -> fetch; se for criar -> reset
    LaunchedEffect(cleanDocId) {
        if (isEditMode) {
            vm.setDocId(cleanDocId)
            vm.fetch(cleanDocId!!)
        } else {
            vm.resetForCreate()
        }
    }

    ProductDetailViewContent(
        modifier = modifier,
        uiState = uiState,
        isEditMode = isEditMode,
        isReadOnly = isReadOnly,
        onSkuChange = vm::setSku,
        onNameChange = vm::setName,
        onFamiliaChange = vm::setFamilia,
        onUnidadeChange = vm::setUnidade,
        onLocalizacaoChange = vm::setLocalizacao,
        onStockMinimoChange = vm::setStockMinimo,
        onStockAtualChange = vm::setStockAtual,
        onEansChange = vm::setEans,
        onNotasChange = vm::setNotas,
        onEanScanned = vm::addEanScanned,
        onSave = { vm.save { navController.popBackStack() } },
        onDelete = { vm.delete { navController.popBackStack() } },
        onCancel = { navController.popBackStack() }
    )
}

@Composable
fun ProductDetailViewContent(
    modifier: Modifier = Modifier,
    uiState: ProductDetailState,
    isEditMode: Boolean,
    isReadOnly: Boolean = false,
    onSkuChange: (String) -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onFamiliaChange: (String) -> Unit = {},
    onUnidadeChange: (String) -> Unit = {},
    onLocalizacaoChange: (String) -> Unit = {},
    onStockMinimoChange: (String) -> Unit = {},
    onStockAtualChange: (String) -> Unit = {},
    onEansChange: (String) -> Unit = {},
    onNotasChange: (String) -> Unit = {},
    onEanScanned: (String) -> Unit = {},
    onSave: () -> Unit = {},
    onDelete: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var showScanner by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.cores),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 28.dp, bottom = 92.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when {
                    isReadOnly -> "Detalhes do Artigo"
                    isEditMode -> "Editar Artigo"
                    else -> "Criar Artigo"
                },
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(14.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {

                    if (uiState.isLoading) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(color = Color.Black)
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    if (uiState.error != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(10.dp))
                    }

                    Field("SKU *", uiState.sku, onSkuChange, readOnly = isReadOnly)
                    Field("Nome *", uiState.name, onNameChange, readOnly = isReadOnly)
                    Field("Família *", uiState.familia, onFamiliaChange, readOnly = isReadOnly)
                    Field("Unidade *", uiState.unidade, onUnidadeChange, readOnly = isReadOnly)
                    Field("Localização", uiState.localizacao, onLocalizacaoChange, readOnly = isReadOnly)
                    Field("Stock mínimo", uiState.stockMinimo, onStockMinimoChange, readOnly = isReadOnly)
                    Field("Stock atual", uiState.stockAtual, onStockAtualChange, readOnly = isReadOnly)

                    Text("EANs (separados por vírgulas)", color = Color.Black)
                    Spacer(Modifier.height(6.dp))

                    OutlinedTextField(
                        value = uiState.eans,
                        onValueChange = onEansChange,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = fieldColors(),
                        readOnly = isReadOnly
                    )

                    Spacer(Modifier.height(8.dp))

                    // Botão scanner só para admin
                    if (!isReadOnly) {
                        Button(
                            onClick = { showScanner = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0B1220),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Ler EAN/QR pela câmara")
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.notas,
                        onValueChange = onNotasChange,
                        label = { Text("Notas") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 110.dp),
                        minLines = 4,
                        colors = fieldColors(),
                        readOnly = isReadOnly
                    )

                    Spacer(Modifier.height(14.dp))

                    // Botões de ação só para admin
                    if (!isReadOnly) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onSave,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0B1220),
                                    contentColor = Color.White
                                ),
                                enabled = !uiState.isLoading
                            ) {
                                Text(if (isEditMode) "Guardar" else "Criar")
                            }

                            Button(
                                onClick = onCancel,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEDEDED),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("Cancelar")
                            }
                        }

                        if (isEditMode) {
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = onDelete,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFE6E6),
                                    contentColor = Color.Black
                                ),
                                enabled = !uiState.isLoading
                            ) {
                                Text("Eliminar")
                            }
                        }
                    } else {
                        // User: apenas botão voltar
                        Button(
                            onClick = onCancel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Voltar")
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp, top = 14.dp),
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

        if (showScanner) {
            ScannerOverlay(
                onClose = { showScanner = false },
                onCodeScanned = { code ->
                    onEanScanned(code)
                    showScanner = false
                }
            )
        }
    }
}

@Composable
private fun Field(label: String, value: String, onValueChange: (String) -> Unit, readOnly: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        singleLine = true,
        colors = fieldColors(),
        readOnly = readOnly
    )
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White
)

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    LojaSocialTheme {
        ProductDetailViewContent(
            uiState = ProductDetailState(
                sku = "ABC123",
                name = "Massa Espirais",
                familia = "Massas",
                unidade = "Unidade",
                localizacao = "A1-03",
                stockMinimo = "5",
                stockAtual = "10",
                eans = "5601234567890",
                notas = "Notas de teste",
                isLoading = false
            ),
            isEditMode = true
        )
    }
}