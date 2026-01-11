package com.example.lojasocial.ui.agendas

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.R
import com.example.lojasocial.ui.theme.LojaSocialTheme
import java.util.Calendar

@Composable
fun AgendasCreateView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vm: AgendasCreateViewModel = viewModel()
    val uiState = vm.uiState.value

    AgendasCreateViewContent(
        modifier = modifier,
        uiState = uiState,
        onEntityChange = vm::setEntity,
        onDateChange = vm::setDate,
        onSaveClick = {
            vm.create {
                navController.popBackStack() // volta à lista
            }
        },
        onCancelClick = { navController.popBackStack() }
    )
}

@Composable
fun AgendasCreateViewContent(
    modifier: Modifier = Modifier,
    uiState: AgendasCreateState,
    onEntityChange: (String) -> Unit = {},
    onDateChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()
    val green = Color(0xFF2E7D32)

    fun openPicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, y, m, d ->
                val mm = (m + 1).toString().padStart(2, '0')
                val dd = d.toString().padStart(2, '0')
                onDateChange("$y-$mm-$dd")
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
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
                .padding(horizontal = 24.dp)
                .padding(top = 70.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Nova Agenda",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scroll)
                        .padding(16.dp)
                        .imePadding()
                ) {

                    if (uiState.error != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = uiState.entity ?: "",
                        onValueChange = onEntityChange,
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
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

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.date ?: "",
                        onValueChange = {},
                        label = { Text("Data (yyyy-MM-dd)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !uiState.isLoading) { openPicker() },
                        readOnly = true,
                        enabled = false,
                        trailingIcon = {
                            IconButton(
                                onClick = { openPicker() },
                                enabled = !uiState.isLoading
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = "Escolher data")
                            }
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledBorderColor = green.copy(alpha = 0.75f),
                            disabledLabelColor = green.copy(alpha = 0.75f),
                            disabledContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        Button(
                            onClick = onSaveClick,
                            enabled = !uiState.isLoading,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0B1220),
                                contentColor = Color.White
                            )
                        ) {
                            Text(if (uiState.isLoading) "A guardar..." else "Guardar")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        OutlinedButton(
                            onClick = onCancelClick,
                            enabled = !uiState.isLoading,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AgendasCreateViewPreview() {
    LojaSocialTheme {
        AgendasCreateViewContent(
            uiState = AgendasCreateState(
                entity = "Maria Alves",
                date = "2026-01-15"
            )
        )
    }
}