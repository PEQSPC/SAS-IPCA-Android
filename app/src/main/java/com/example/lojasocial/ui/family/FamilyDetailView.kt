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
import androidx.compose.material.icons.filled.Person
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
import com.example.lojasocial.AppConstansts
import com.example.lojasocial.R
import com.example.lojasocial.ui.theme.LojaSocialTheme

private enum class BottomTab2 {
    Products, Beneficiaries, CreateBeneficiary, Families, Profile, Admin
}

@Composable
fun FamilyDetailView(
    navController: NavController,
    docId: String,
    modifier: Modifier = Modifier
) {
    val vm: FamilyDetailViewModel = viewModel()
    val uiState = vm.uiState.value

    LaunchedEffect(docId) { vm.fetch(docId) }

    FamilyDetailViewContent(
        navController = navController,
        modifier = modifier,
        uiState = uiState,
        onNameChange = vm::setName,
        onNotesChange = vm::setNotes,
        onSaveClick = {
            vm.save {
                navController.popBackStack() // ✅ volta à lista
            }
        },
        onCancelClick = { navController.popBackStack() },
        onDeleteClick = {
            vm.delete {
                navController.popBackStack() // ✅ volta à lista
            }
        }
    )
}

@Composable
fun FamilyDetailViewContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    uiState: FamilyDetailState,
    onNameChange: (String) -> Unit = {},
    onNotesChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val scroll = rememberScrollState()
    val selectedTab = BottomTab2.Families
    val green = Color(0xFF2E7D32)

    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.img),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 70.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Detalhe da Família",
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

                        OutlinedTextField(
                            value = uiState.name,
                            onValueChange = onNameChange,
                            label = { Text("Nome") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
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

                        OutlinedTextField(
                            value = uiState.notes,
                            onValueChange = onNotesChange,
                            label = { Text("Notas") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            minLines = 2,
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

                        if (uiState.createdAt != null || uiState.updatedAt != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Criado: ${uiState.createdAt ?: "—"}", color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
                            Text("Atualizado: ${uiState.updatedAt ?: "—"}", color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
                        }

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

                        Spacer(modifier = Modifier.height(10.dp))

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

            FamilyDetailBottomBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                selected = selectedTab,
                onSelect = { tab ->
                    when (tab) {
                        BottomTab2.Products -> navController.navigate(AppConstansts.products) { launchSingleTop = true }
                        BottomTab2.Beneficiaries -> navController.navigate(AppConstansts.beneficiaries) { launchSingleTop = true }
                        BottomTab2.CreateBeneficiary -> navController.navigate(AppConstansts.createBeneficiary) { launchSingleTop = true }
                        BottomTab2.Families -> navController.navigate(AppConstansts.families) { launchSingleTop = true }
                        BottomTab2.Profile -> navController.navigate(AppConstansts.profile) { launchSingleTop = true }
                        BottomTab2.Admin -> navController.navigate(AppConstansts.adminHome) { launchSingleTop = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun FamilyDetailBottomBar(
    modifier: Modifier = Modifier,
    selected: BottomTab2,
    onSelect: (BottomTab2) -> Unit
) {
    NavigationBar(
        modifier = modifier.height(80.dp),
        containerColor = Color(0xFFDFF3E3),
        tonalElevation = 6.dp,
        windowInsets = WindowInsets(0)
    ) {
        FamilyDetailBottomItem(BottomTab2.Products, selected, Icons.Default.Inventory2, "Produtos", onSelect)
        FamilyDetailBottomItem(BottomTab2.Beneficiaries, selected, Icons.Default.Group, "Benef.", onSelect)
        FamilyDetailBottomItem(BottomTab2.CreateBeneficiary, selected, Icons.Default.PersonAdd, "Criar", onSelect)
        FamilyDetailBottomItem(BottomTab2.Families, selected, Icons.Default.People, "Famílias", onSelect)
        FamilyDetailBottomItem(BottomTab2.Profile, selected, Icons.Default.Person, "Perfil", onSelect)
        FamilyDetailBottomItem(BottomTab2.Admin, selected, Icons.Default.Home, "Admin", onSelect)
    }
}

@Composable
private fun RowScope.FamilyDetailBottomItem(
    tab: BottomTab2,
    selected: BottomTab2,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onSelect: (BottomTab2) -> Unit
) {
    NavigationBarItem(
        selected = tab == selected,
        onClick = { onSelect(tab) },
        alwaysShowLabel = true,
        icon = { Icon(icon, contentDescription = label) },
        label = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun FamilyDetailViewPreview() {
    LojaSocialTheme {
        FamilyDetailViewContent(
            navController = rememberNavController(),
            uiState = FamilyDetailState(
                name = "Família A",
                notes = "Notas exemplo",
                createdAt = "08/01/2026 15:00",
                updatedAt = "08/01/2026 15:10"
            )
        )
    }
}