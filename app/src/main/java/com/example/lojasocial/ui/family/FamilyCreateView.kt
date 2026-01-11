package com.example.lojasocial.ui.family

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.AppConstants
import com.example.lojasocial.R
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun FamilyCreateView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vm: FamilyCreateViewModel = viewModel()
    val uiState = vm.uiState.value

    FamilyCreateViewContent(
        navController = navController,
        modifier = modifier,
        uiState = uiState,
        onNameChange = vm::setName,
        onNotesChange = vm::setNotes,
        onSaveClick = {
            vm.create {
                // ✅ volta à lista de famílias
                navController.navigate(AppConstants.families) {
                    launchSingleTop = true
                    popUpTo(AppConstants.families) { inclusive = true }
                }
            }
        },
        onCancelClick = { navController.popBackStack() }
    )
}

@Composable
fun FamilyCreateViewContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    uiState: FamilyCreateState,
    onNameChange: (String) -> Unit = {},
    onNotesChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val scroll = rememberScrollState()
    val green = Color(0xFF2E7D32)

    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.img),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // -------- CONTEÚDO --------
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 70.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Criar Família",
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

                        if (uiState.isLoading) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
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

                        OutlinedTextField(
                            value = uiState.name ?: "",
                            onValueChange = onNameChange,
                            label = { Text("Nome") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !uiState.isLoading,
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
                            value = uiState.notes ?: "",
                            onValueChange = onNotesChange,
                            label = { Text("Notas") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            enabled = !uiState.isLoading,
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

            // -------- BOTTOM BAR (SEM ENUM) --------
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .navigationBarsPadding(),
                containerColor = Color(0xFFDFF3E3),
                tonalElevation = 6.dp,
                windowInsets = WindowInsets(0)
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(AppConstants.products) { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Produtos") },
                    label = { Text("Produtos", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(AppConstants.beneficiaries) { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Group, contentDescription = "Benef.") },
                    label = { Text("Benef.", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(AppConstants.createBeneficiary) { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Criar") },
                    label = { Text("Criar", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )

                // ✅ Nesta view estamos nas Famílias
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.navigate(AppConstants.families) { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.People, contentDescription = "Famílias") },
                    label = { Text("Famílias", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(AppConstants.adminHome) { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Admin") },
                    label = { Text("Admin", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FamilyCreateViewPreview() {
    LojaSocialTheme {
        FamilyCreateViewContent(
            navController = rememberNavController(),
            uiState = FamilyCreateState(
                name = "Família A",
                notes = "Notas de teste",
                isLoading = false,
                error = null
            )
        )
    }
}