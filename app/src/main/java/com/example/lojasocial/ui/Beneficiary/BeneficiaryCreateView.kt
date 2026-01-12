package com.example.lojasocial.ui.beneficiary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.R
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun BeneficiaryCreateView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vm: BeneficiaryCreateViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()

    BeneficiaryCreateViewContent(
        modifier = modifier,
        uiState = uiState,
        onNumeroAlunoChange = vm::setNumeroAluno,
        onNomeChange = vm::setNome,
        onNifChange = vm::setNif,
        onDataNascimentoChange = vm::setDataNascimento,
        onEmailChange = vm::setEmail,
        onCursoChange = vm::setCurso,
        onAnoChange = vm::setAno,
        onPhoneChange = vm::setPhone,
        onSaveClick = { vm.create { navController.popBackStack() } },
        onCancelClick = { navController.popBackStack() }
    )
}

@Composable
fun BeneficiaryCreateViewContent(
    modifier: Modifier = Modifier,
    uiState: BeneficiaryCreateState,
    onNumeroAlunoChange: (String) -> Unit = {},
    onNomeChange: (String) -> Unit = {},
    onNifChange: (String) -> Unit = {},
    onDataNascimentoChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onCursoChange: (String) -> Unit = {},
    onAnoChange: (String) -> Unit = {},
    onPhoneChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val scroll = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Criar Beneficiário",
                color = Color.Black,
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

                    FieldGreen("Nº Aluno", uiState.numeroAluno, onNumeroAlunoChange)
                    FieldGreen("Nome", uiState.nome, onNomeChange)
                    FieldGreen("NIF", uiState.nif, onNifChange)
                    FieldGreen("Data Nascimento (dd/MM/yyyy)", uiState.dataNascimento, onDataNascimentoChange)
                    FieldGreen("Email", uiState.email, onEmailChange)
                    FieldGreen("Curso", uiState.curso, onCursoChange)
                    FieldGreen("Ano Curricular", uiState.ano, onAnoChange)

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
                            shape = RoundedCornerShape(14.dp),
                            enabled = !uiState.isLoading
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldGreen(
    label: String,
    value: String?,
    onValueChange: (String) -> Unit
) {
    val green = Color(0xFF2E7D32)

    OutlinedTextField(
        value = value ?: "",
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
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
}

@Preview(showBackground = true)
@Composable
fun BeneficiaryCreateViewPreview() {
    LojaSocialTheme {
        BeneficiaryCreateViewContent(
            uiState = BeneficiaryCreateState(
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