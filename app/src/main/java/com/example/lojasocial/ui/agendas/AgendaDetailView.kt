package com.example.lojasocial.ui.agendas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.R
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun AgendaDetailView(
    navController: NavController,
    docId: String,
    modifier: Modifier = Modifier
) {
    val vm: AgendaDetailViewModel = viewModel()
    val uiState = vm.uiState.value

    LaunchedEffect(docId) { vm.fetch(docId) }

    AgendaDetailViewContent(
        modifier = modifier,
        uiState = uiState,
        onDateChange = vm::setDate,
        onTimeWindowChange = vm::setTimeWindow,
        onTypeChange = vm::setType,
        onStatusChange = vm::setStatus,
        onEntityChange = vm::setEntity,
        onAddressChange = vm::setAddress,
        onNotesChange = vm::setNotes,
        onSaveClick = {
            vm.save(onSuccess = { navController.popBackStack() }) // ✅ volta à lista
        },
        onCancelClick = { navController.popBackStack() },
        onDeleteClick = {
            vm.delete(onDeleted = { navController.popBackStack() })
        }
    )
}

@Composable
fun AgendaDetailViewContent(
    modifier: Modifier = Modifier,
    uiState: AgendaDetailState,
    onDateChange: (String) -> Unit = {},
    onTimeWindowChange: (String) -> Unit = {},
    onTypeChange: (String) -> Unit = {},
    onStatusChange: (String) -> Unit = {},
    onEntityChange: (String) -> Unit = {},
    onAddressChange: (String) -> Unit = {},
    onNotesChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val scroll = rememberScrollState()

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
                .padding(horizontal = 24.dp)
                .padding(top = 70.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Detalhe da Agenda",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scroll)
                        .padding(16.dp)
                        .imePadding()
                ) {

                    if (uiState.isLoading) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (uiState.error != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Field("Data (yyyy-MM-dd)", uiState.date, onDateChange)
                    Field("Janela (ex: 09:00-12:00)", uiState.timeWindow, onTimeWindowChange)
                    Field("Tipo (Entrega/Recolha/Doação)", uiState.type, onTypeChange)
                    Field("Estado (Planeado/Confirmado)", uiState.status, onStatusChange)
                    Field("Entidade", uiState.entity, onEntityChange)
                    Field("Morada", uiState.address, onAddressChange)
                    Field("Notas", uiState.notes, onNotesChange, singleLine = false)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        Button(
                            onClick = onSaveClick,
                            enabled = !uiState.isLoading,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0B1220),
                                contentColor = Color.White
                            )
                        ) { Text("Guardar") }

                        Spacer(modifier = Modifier.width(12.dp))

                        OutlinedButton(
                            onClick = onCancelClick,
                            enabled = !uiState.isLoading,
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Cancelar") }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onDeleteClick,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFE6E6),
                            contentColor = Color.Black
                        )
                    ) { Text("Eliminar") }
                }
            }
        }
    }
}

@Composable
private fun Field(
    label: String,
    value: String?,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value ?: "",
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 2,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AgendaDetailPreview() {
    LojaSocialTheme {
        AgendaDetailViewContent(
            uiState = AgendaDetailState(
                date = "2025-11-15",
                timeWindow = "09:00-12:00",
                type = "Entrega",
                status = "Planeado",
                entity = "Maria Alves",
                address = "Rua do Instituto, 123",
                notes = "Notas exemplo",
                isLoading = false,
                error = null
            )
        )
    }
}