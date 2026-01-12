package com.example.lojasocial.ui.admin.deliveries

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.ui.theme.LojaSocialTheme
import java.util.Calendar

@Composable
fun DeliveryCreateView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vm: DeliveryCreateViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }

    DeliveryCreateViewContent(
        navController = navController,
        modifier = modifier,
        uiState = uiState,
        onBeneficiaryIdChange = vm::setBeneficiaryId,
        onScheduledAtTextChange = vm::setScheduledAtText,
        onAddLine = vm::addLine,
        onRemoveLine = vm::removeLine,
        onUpdateLine = vm::updateLine,
        onSaveClick = vm::save
    )
}

@Composable
fun DeliveryCreateViewContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    uiState: DeliveryCreateState,
    onBeneficiaryIdChange: (String) -> Unit = {},
    onScheduledAtTextChange: (String) -> Unit = {},
    onAddLine: () -> Unit = {},
    onRemoveLine: (Int) -> Unit = {},
    onUpdateLine: (Int, DeliveryLineUI) -> Unit = { _, _ -> },
    onSaveClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val green = Color(0xFF2E7D32)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // BACK BUTTON + TITLE
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.Black)
                }
                Text(
                    text = "Planear Entrega",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // BENEFICIÁRIO (Dropdown)
                    val expandedBeneficiary = remember { mutableStateOf(false) }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = uiState.beneficiaryNameText,
                            onValueChange = {},
                            label = { Text("Beneficiário") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedBeneficiary.value = true },
                            enabled = false,
                            readOnly = true,
                            shape = RoundedCornerShape(18.dp),
                            trailingIcon = {
                                IconButton(onClick = { expandedBeneficiary.value = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir menu")
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledContainerColor = Color.White,
                                disabledBorderColor = green.copy(alpha = 0.75f),
                                disabledLabelColor = green.copy(alpha = 0.75f),
                                disabledTrailingIconColor = green
                            )
                        )

                        DropdownMenu(
                            expanded = expandedBeneficiary.value,
                            onDismissRequest = { expandedBeneficiary.value = false }
                        ) {
                            uiState.availableBeneficiaries.forEach { beneficiary ->
                                DropdownMenuItem(
                                    text = { Text(beneficiary.nome ?: "—") },
                                    onClick = {
                                        onBeneficiaryIdChange(beneficiary.docId ?: "")
                                        expandedBeneficiary.value = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // DATA PLANEADA (DatePicker)
                    val calendar = Calendar.getInstance()

                    fun openDatePicker() {
                        val year: Int
                        val month: Int
                        val day: Int

                        if (uiState.scheduledAtText.isNotBlank()) {
                            val parts = uiState.scheduledAtText.split("/")
                            if (parts.size == 3) {
                                day = parts[0].toIntOrNull() ?: calendar.get(Calendar.DAY_OF_MONTH)
                                month = (parts[1].toIntOrNull() ?: (calendar.get(Calendar.MONTH) + 1)) - 1
                                year = parts[2].toIntOrNull() ?: calendar.get(Calendar.YEAR)
                            } else {
                                day = calendar.get(Calendar.DAY_OF_MONTH)
                                month = calendar.get(Calendar.MONTH)
                                year = calendar.get(Calendar.YEAR)
                            }
                        } else {
                            day = calendar.get(Calendar.DAY_OF_MONTH)
                            month = calendar.get(Calendar.MONTH)
                            year = calendar.get(Calendar.YEAR)
                        }

                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                val dia = d.toString().padStart(2, '0')
                                val mes = (m + 1).toString().padStart(2, '0')
                                onScheduledAtTextChange("$dia/$mes/$y")
                            },
                            year, month, day
                        ).show()
                    }

                    OutlinedTextField(
                        value = uiState.scheduledAtText,
                        onValueChange = {},
                        label = { Text("Data planeada") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { openDatePicker() },
                        readOnly = true,
                        enabled = false,
                        shape = RoundedCornerShape(18.dp),
                        trailingIcon = {
                            IconButton(onClick = { openDatePicker() }) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = "Selecionar data")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledContainerColor = Color.White,
                            disabledBorderColor = green.copy(alpha = 0.75f),
                            disabledLabelColor = green.copy(alpha = 0.75f),
                            disabledTrailingIconColor = green
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // LINHAS DE PRODUTOS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Produtos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        IconButton(onClick = onAddLine) {
                            Icon(Icons.Default.Add, contentDescription = "Adicionar produto", tint = green)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    uiState.lines.forEachIndexed { index, line ->
                        DeliveryLineCard(
                            line = line,
                            index = index,
                            availableItems = uiState.availableItems,
                            onUpdate = { updatedLine -> onUpdateLine(index, updatedLine) },
                            onRemove = { onRemoveLine(index) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (uiState.lines.isEmpty()) {
                        Text(
                            text = "Nenhum produto adicionado. Carrega em + para adicionar.",
                            color = Color.Black.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = uiState.error ?: "",
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0B1220),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Planear Entrega")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DeliveryLineCard(
    line: DeliveryLineUI,
    index: Int,
    availableItems: List<com.example.lojasocial.models.Item>,
    onUpdate: (DeliveryLineUI) -> Unit,
    onRemove: () -> Unit
) {
    val green = Color(0xFF2E7D32)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Produto ${index + 1}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Remover", tint = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ITEM (Dropdown)
            val expandedItem = remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = line.itemName,
                    onValueChange = {},
                    label = { Text("Item") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedItem.value = true },
                    enabled = false,
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { expandedItem.value = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir menu")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledContainerColor = Color.White,
                        disabledBorderColor = green.copy(alpha = 0.75f),
                        disabledLabelColor = green.copy(alpha = 0.75f),
                        disabledTrailingIconColor = green
                    )
                )

                DropdownMenu(
                    expanded = expandedItem.value,
                    onDismissRequest = { expandedItem.value = false }
                ) {
                    availableItems.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.name ?: "—") },
                            onClick = {
                                onUpdate(line.copy(itemId = item.docId ?: "", itemName = item.name ?: ""))
                                expandedItem.value = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // QUANTIDADE
            OutlinedTextField(
                value = line.quantityText,
                onValueChange = { onUpdate(line.copy(quantityText = it)) },
                label = { Text("Quantidade") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = green,
                    focusedBorderColor = green,
                    unfocusedBorderColor = green.copy(alpha = 0.75f),
                    focusedLabelColor = green,
                    unfocusedLabelColor = green.copy(alpha = 0.75f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }
    }
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
fun DeliveryCreateViewPreview() {
    LojaSocialTheme {
        DeliveryCreateViewContent(
            navController = rememberNavController(),
            uiState = DeliveryCreateState(
                beneficiaryNameText = "Maria Silva",
                scheduledAtText = "20/01/2025",
                lines = listOf(
                    DeliveryLineUI(itemName = "Arroz", quantityText = "5"),
                    DeliveryLineUI(itemName = "Feijão", quantityText = "3")
                )
            )
        )
    }
}
