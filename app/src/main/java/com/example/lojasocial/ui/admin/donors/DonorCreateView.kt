package com.example.lojasocial.ui.admin.donors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.models.Donor
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun DonorCreateView(
    navController: NavController,
    docId: String? = null,
    modifier: Modifier = Modifier
) {
    val vm: DonorCreateViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(docId) {
        if (docId != null) {
            vm.loadDonor(docId)
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }

    DonorCreateViewContent(
        navController = navController,
        modifier = modifier,
        uiState = uiState,
        isEditing = docId != null,
        onTypeChange = vm::setType,
        onNameChange = vm::setName,
        onEmailChange = vm::setEmail,
        onNifChange = vm::setNif,
        onSaveClick = vm::save
    )
}

@Composable
fun DonorCreateViewContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    uiState: DonorCreateState,
    isEditing: Boolean = false,
    onTypeChange: (String) -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onNifChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
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
                    text = if (isEditing) "Editar Doador" else "Novo Doador",
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
                    // TIPO (Dropdown)
                    val expandedType = remember { mutableStateOf(false) }
                    val types = listOf("COMPANY", "PRIVATE")
                    val typeLabels = mapOf("COMPANY" to "Empresa", "PRIVATE" to "Particular")

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = typeLabels[uiState.donor.type] ?: "",
                            onValueChange = {},
                            label = { Text("Tipo") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedType.value = true },
                            enabled = false,
                            readOnly = true,
                            shape = RoundedCornerShape(18.dp),
                            trailingIcon = {
                                IconButton(onClick = { expandedType.value = true }) {
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
                            expanded = expandedType.value,
                            onDismissRequest = { expandedType.value = false }
                        ) {
                            types.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(typeLabels[type] ?: type) },
                                    onClick = {
                                        onTypeChange(type)
                                        expandedType.value = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // NOME
                    OutlinedTextField(
                        value = uiState.donor.name ?: "",
                        onValueChange = onNameChange,
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth(),
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

                    // EMAIL
                    OutlinedTextField(
                        value = uiState.donor.email ?: "",
                        onValueChange = onEmailChange,
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
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

                    // NIF
                    OutlinedTextField(
                        value = uiState.donor.nif ?: "",
                        onValueChange = onNifChange,
                        label = { Text("NIF") },
                        modifier = Modifier.fillMaxWidth(),
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
                            Text(if (isEditing) "Atualizar" else "Criar")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DonorCreateViewPreview() {
    LojaSocialTheme {
        DonorCreateViewContent(
            navController = rememberNavController(),
            uiState = DonorCreateState(
                donor = Donor(type = "COMPANY", name = "Empresa ABC"),
                isLoading = false
            ),
            isEditing = false
        )
    }
}
