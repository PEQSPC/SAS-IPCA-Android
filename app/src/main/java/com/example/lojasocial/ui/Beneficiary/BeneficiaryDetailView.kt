package com.example.lojasocial.ui.beneficiary

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
import com.example.lojasocial.R
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun BeneficiaryDetailView(
    navController: NavController,
    docId: String,
    modifier: Modifier = Modifier
) {
    val vm: BeneficiaryDetailViewModel = viewModel()
    val uiState = vm.uiState.value

    LaunchedEffect(docId) { vm.fetch(docId) }

    BeneficiaryDetailViewContent(
        modifier = modifier,
        uiState = uiState,
        onNumeroAlunoChange = vm::setNumeroAluno,
        onNomeChange = vm::setNome,
        onNifChange = vm::setNif,
        onDataNascimentoChange = vm::setDataNascimento,
        onEmailChange = vm::setEmail,
        onCursoChange = vm::setCurso,
        onAnoChange = vm::setAno,
        onSaveClick = { vm.save { navController.popBackStack() } }, // ✅ VOLTA À LISTA
        onCancelClick = { navController.popBackStack() },
        onDeleteClick = { vm.delete { navController.popBackStack() } }
    )
}

@Composable
fun BeneficiaryDetailViewContent(
    modifier: Modifier = Modifier,
    uiState: BeneficiaryDetailState,
    onNumeroAlunoChange: (String) -> Unit = {},
    onNomeChange: (String) -> Unit = {},
    onNifChange: (String) -> Unit = {},
    onDataNascimentoChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onCursoChange: (String) -> Unit = {},
    onAnoChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

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
                .padding(top = 50.dp, bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Detalhe do Beneficiário",
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
                        .verticalScroll(scrollState)
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

                    Field("Nº Aluno", uiState.numeroAluno, onNumeroAlunoChange)
                    Field("Nome", uiState.nome, onNomeChange)
                    Field("NIF", uiState.nif, onNifChange)
                    Field("Data Nascimento (dd/MM/yyyy)", uiState.dataNascimento, onDataNascimentoChange)
                    Field("Email", uiState.email, onEmailChange)
                    Field("Curso", uiState.curso, onCursoChange)
                    Field("Ano Curricular", uiState.ano, onAnoChange)

                    Spacer(modifier = Modifier.height(8.dp))

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

                    Spacer(modifier = Modifier.height(8.dp))

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
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value ?: "",
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        singleLine = true,
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
fun BeneficiaryDetailPreview() {
    LojaSocialTheme {
        BeneficiaryDetailViewContent(
            uiState = BeneficiaryDetailState(
                numeroAluno = "A12345",
                nome = "Maria Alves",
                nif = "234567890",
                dataNascimento = "12/04/2001",
                email = "maria@aluno.pt",
                curso = "LESI",
                ano = "2º ano"
            )
        )
    }
}