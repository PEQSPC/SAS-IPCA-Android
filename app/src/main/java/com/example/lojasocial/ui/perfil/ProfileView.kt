package com.example.lojasocial.ui.perfil

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.lojasocial.AppConstants
import com.example.lojasocial.R
import com.example.lojasocial.models.Profile
import com.example.lojasocial.ui.theme.LojaSocialTheme
import java.io.File
import java.util.Calendar

// ---------------- BOTTOM NAV ----------------

private enum class BottomTab {
    Products, Beneficiaries, CreateBeneficiary, Profile, Admin
}

@Composable
private fun AppBottomBar(
    modifier: Modifier = Modifier,
    selected: BottomTab,
    onSelect: (BottomTab) -> Unit
) {
    NavigationBar(
        modifier = modifier.height(80.dp),
        containerColor = Color(0xFFDFF3E3),
        tonalElevation = 6.dp,
        windowInsets = WindowInsets(0)
    ) {
        BottomItem(BottomTab.Products, selected, Icons.Default.Inventory2, "Produtos", onSelect)
        BottomItem(BottomTab.Beneficiaries, selected, Icons.Default.Group, "Benef.", onSelect)
        BottomItem(BottomTab.CreateBeneficiary, selected, Icons.Default.PersonAdd, "Criar", onSelect)
        BottomItem(BottomTab.Profile, selected, Icons.Default.Person, "Perfil", onSelect)
        BottomItem(BottomTab.Admin, selected, Icons.Default.Home, "Admin", onSelect)
    }
}

@Composable
private fun RowScope.BottomItem(
    tab: BottomTab,
    selected: BottomTab,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onSelect: (BottomTab) -> Unit
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

// ---------------- VIEW ----------------

@Composable
fun ProfileView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: ProfileViewModel = viewModel()
    val uiState by viewModel.uiState
    val context = LocalContext.current

    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) viewModel.setPhotoUri(uri.toString())
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri.value != null) {
            viewModel.setPhotoUri(cameraImageUri.value.toString())
        }
    }

    LaunchedEffect(Unit) { viewModel.loadProfile() }

    ProfileViewContent(
        navController = navController,
        modifier = modifier,
        uiState = uiState,
        onNomeChange = viewModel::setNome,
        onSobrenomeChange = viewModel::setSobrenome,
        onGeneroChange = viewModel::setGenero,
        onIdadeChange = viewModel::setIdadeText,
        onNifChange = viewModel::setNifText,
        onSelectFromGallery = { galleryLauncher.launch("image/*") },
        onTakePhoto = {
            val uri = createImageFileUri(context)
            cameraImageUri.value = uri
            cameraLauncher.launch(uri)
        },
        onSaveClick = viewModel::saveProfile
    )
}

@Composable
fun ProfileViewContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    uiState: ProfileState,
    onNomeChange: (String) -> Unit = {},
    onSobrenomeChange: (String) -> Unit = {},
    onGeneroChange: (String) -> Unit = {},
    onIdadeChange: (String) -> Unit = {},
    onNifChange: (String) -> Unit = {},
    onSelectFromGallery: () -> Unit = {},
    onTakePhoto: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // ✅ nesta screen fica selecionado Perfil
    val selectedTab = BottomTab.Profile

    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.img),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // --------- CONTEÚDO (COM SCROLL) ---------
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 40.dp, bottom = 16.dp)
                    .verticalScroll(scrollState)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ✅ QUADRADO/CAIXA BRANCA (como no criar beneficiários)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Perfil",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ---------- FOTO DE PERFIL + MENU ----------
                        val showPhotoMenu = remember { mutableStateOf(false) }

                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape) // ✅ garante círculo
                                .clickable { showPhotoMenu.value = true }
                                .border(2.dp, Color(0xFF2E7D32), CircleShape), // verde
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.photoUri != null) {
                                AsyncImage(
                                    model = uiState.photoUri,
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape), // ✅ corta a imagem em círculo
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = "Adicionar foto",
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showPhotoMenu.value,
                                onDismissRequest = { showPhotoMenu.value = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Escolher da galeria") },
                                    onClick = {
                                        showPhotoMenu.value = false
                                        onSelectFromGallery()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Tirar foto") },
                                    onClick = {
                                        showPhotoMenu.value = false
                                        onTakePhoto()
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        val green = Color(0xFF2E7D32)

                        // ---------- CAMPOS ----------
                        OutlinedTextField(
                            value = uiState.profile.nome ?: "",
                            onValueChange = onNomeChange,
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

                        OutlinedTextField(
                            value = uiState.profile.sobrenome ?: "",
                            onValueChange = onSobrenomeChange,
                            label = { Text("Sobrenome") },
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

                        // ---------- DATA NASCIMENTO (DatePicker) ----------
                        val calendar = Calendar.getInstance()

                        val onPickDate = {
                            val year: Int
                            val month: Int
                            val day: Int

                            if (uiState.idadeText.isNotBlank()) {
                                val parts = uiState.idadeText.split("/")
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
                                    onIdadeChange("$dia/$mes/$y")
                                },
                                year, month, day
                            ).show()
                        }

                        OutlinedTextField(
                            value = uiState.idadeText,
                            onValueChange = {},
                            label = { Text("Data de nascimento") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPickDate() },
                            readOnly = true,
                            enabled = false,
                            shape = RoundedCornerShape(18.dp),
                            trailingIcon = {
                                IconButton(onClick = { onPickDate() }) {
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

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = uiState.nifText,
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

                        Spacer(modifier = Modifier.height(10.dp))

                        // ---------- DROPDOWN GÉNERO ----------
                        val expandedGenero = remember { mutableStateOf(false) }
                        val generos = listOf("Masculino", "Feminino", "Outro")

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = uiState.profile.genero ?: "",
                                onValueChange = {},
                                label = { Text("Género") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedGenero.value = true },
                                enabled = false,
                                readOnly = true,
                                shape = RoundedCornerShape(18.dp),
                                trailingIcon = {
                                    IconButton(onClick = { expandedGenero.value = true }) {
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
                                expanded = expandedGenero.value,
                                onDismissRequest = { expandedGenero.value = false }
                            ) {
                                generos.forEach { genero ->
                                    DropdownMenuItem(
                                        text = { Text(genero) },
                                        onClick = {
                                            onGeneroChange(genero)
                                            expandedGenero.value = false
                                        }
                                    )
                                }
                            }
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

                        if (uiState.isSaved) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Perfil guardado com sucesso!",
                                color = Color(0xFF1B5E20),
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
                                Text("Guardar")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // -------- BOTTOM BAR --------
            AppBottomBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                selected = selectedTab,
                onSelect = { tab ->
                    when (tab) {
                        BottomTab.Products ->
                            navController.navigate(AppConstants.products) { launchSingleTop = true }

                        BottomTab.Beneficiaries ->
                            navController.navigate(AppConstants.beneficiaries) { launchSingleTop = true }

                        BottomTab.CreateBeneficiary ->
                            navController.navigate(AppConstants.createBeneficiary) { launchSingleTop = true }

                        BottomTab.Profile ->
                            navController.navigate(AppConstants.profile) { launchSingleTop = true }

                        BottomTab.Admin ->
                            navController.navigate(AppConstants.adminHome) { launchSingleTop = true }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    LojaSocialTheme {
        ProfileViewContent(
            navController = rememberNavController(),
            uiState = ProfileState(
                profile = Profile(nome = "João", sobrenome = "Silva", genero = "Masculino"),
                idadeText = "01/01/2000",
                nifText = "123456789",
                photoUri = null,
                isLoading = false,
                error = null,
                isSaved = false
            )
        )
    }
}

fun createImageFileUri(context: Context): Uri {
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val image = File.createTempFile("profile_photo_", ".jpg", dir)
    return FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        image
    )
}