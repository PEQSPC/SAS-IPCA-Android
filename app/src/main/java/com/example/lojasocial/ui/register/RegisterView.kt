package com.example.lojasocial.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lojasocial.R
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun RegisterView(
    uiState: RegisterState = RegisterState(),
    onRegisterClick: () -> Unit = {},
    onBackToLoginClick: () -> Unit = {},
    setUserName: (username: String) -> Unit = {},
    setEmail: (email: String) -> Unit = {},
    setPassword: (password: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {

        // Fundo (igual ao Login)
        Image(
            painter = painterResource(R.drawable.img),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // 🖼️ Logo no topo (igual ao Login)
            Image(
                painter = painterResource(id = R.drawable.ipca1), // troca para o teu logo
                contentDescription = "Registo",
                modifier = Modifier
                    .height(140.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    if (uiState.error != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = uiState.username ?: "",
                        onValueChange = setUserName,
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = greenFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.email ?: "",
                        onValueChange = setEmail,
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = greenFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.password ?: "",
                        onValueChange = setPassword,
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = greenFieldColors(),
                        visualTransformation = if (isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            val icon: ImageVector =
                                if (isPasswordVisible) Icons.Default.Visibility
                                else Icons.Default.VisibilityOff

                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(imageVector = icon, contentDescription = null)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onRegisterClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0B1220),
                                contentColor = Color.White
                            ),
                            enabled = uiState.isLoading != true
                        ) {
                            if (uiState.isLoading == true) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Registar")
                            }
                        }

                        Button(
                            onClick = onBackToLoginClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEDEDED),
                                contentColor = Color.Black
                            ),
                            enabled = uiState.isLoading != true
                        ) {
                            Text("Voltar")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}

/**
 * ✅ Linhas verdes: borda verde em focus e sem focus.
 * Se quiseres só verde quando está focado, digo-te a alteração.
 */
@Composable
private fun greenFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,

    focusedBorderColor = Color(0xFF2E7D32),   // verde
    unfocusedBorderColor = Color(0xFF2E7D32), // verde
    cursorColor = Color(0xFF2E7D32)
)

@Preview(showBackground = true)
@Composable
fun RegisterViewPreview() {
    LojaSocialTheme {
        RegisterView(
            uiState = RegisterState(
                username = "joao",
                email = "joao@email.com",
                password = "123456",
                error = null,
                isLoading = false
            )
        )
    }
}