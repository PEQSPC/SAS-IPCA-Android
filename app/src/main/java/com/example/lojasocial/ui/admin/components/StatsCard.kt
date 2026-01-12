package com.example.lojasocial.ui.admin.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lojasocial.ui.theme.LojaSocialTheme

/**
 * Card de estatística para dashboard
 *
 * @param title Título do card (ex: "Artigos")
 * @param value Valor principal a mostrar (ex: "3")
 * @param subtitle Subtítulo/descrição (ex: "2 em alerta")
 * @param icon Ícone opcional a mostrar
 * @param iconTint Cor do ícone
 * @param modifier Modifier opcional
 */
@Composable
fun StatsCard(
    title: String,
    value: String,
    subtitle: String?,
    icon: ImageVector? = null,
    iconTint: Color = Color(0xFF2E7D32), // Verde escuro por padrão
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Título + Ícone
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Normal
                )

                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Valor principal
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Subtítulo (opcional)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

/**
 * Card de estatística com alerta
 * Mostra uma badge de alerta se houver alertCount > 0
 */
@Composable
fun StatsCardWithAlert(
    title: String,
    value: String,
    alertCount: Int,
    alertLabel: String,
    icon: ImageVector? = null,
    iconTint: Color = Color(0xFF2E7D32),
    modifier: Modifier = Modifier
) {
    val subtitle = if (alertCount > 0) {
        "$alertCount $alertLabel"
    } else {
        "Tudo OK"
    }

    StatsCard(
        title = title,
        value = value,
        subtitle = subtitle,
        icon = icon,
        iconTint = if (alertCount > 0) Color(0xFFFFA726) else iconTint,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun StatsCardPreview() {
    LojaSocialTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Grid de cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    title = "Artigos",
                    value = "3",
                    subtitle = "2 em alerta",
                    icon = Icons.Default.Inventory2,
                    modifier = Modifier.weight(1f)
                )

                StatsCardWithAlert(
                    title = "Doações",
                    value = "5",
                    alertCount = 0,
                    alertLabel = "em alerta",
                    icon = Icons.Default.Inventory2,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    title = "Beneficiários",
                    value = "5",
                    subtitle = "Ativos",
                    icon = Icons.Default.Inventory2,
                    modifier = Modifier.weight(1f)
                )

                StatsCard(
                    title = "Entregas",
                    value = "3",
                    subtitle = "Histórico",
                    icon = Icons.Default.Inventory2,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
