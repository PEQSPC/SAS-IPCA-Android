package com.example.lojasocial.ui.admin.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lojasocial.ui.theme.LojaSocialTheme

/**
 * Badge reutilizável para mostrar status
 *
 * @param text Texto a mostrar no badge
 * @param color Cor do badge (será usada com alpha 0.2f no fundo)
 * @param modifier Modifier opcional
 */
@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Badge para status de entrega
 */
@Composable
fun DeliveryStatusBadge(status: String?, modifier: Modifier = Modifier) {
    val (label, color) = when (status) {
        "SCHEDULED" -> "Planeada" to Color(0xFFFFA726) // Orange
        "DELIVERED" -> "Entregue" to Color(0xFF66BB6A) // Green
        else -> "—" to Color.Gray
    }

    StatusBadge(text = label, color = color, modifier = modifier)
}

/**
 * Badge para status de doação
 */
@Composable
fun DonationStatusBadge(status: String?, modifier: Modifier = Modifier) {
    val (label, color) = when (status) {
        "PENDING" -> "Triagem" to Color(0xFFFFA726) // Orange
        "RECEIVED" -> "Recebida" to Color(0xFF66BB6A) // Green
        "PROCESSED" -> "Processada" to Color(0xFF42A5F5) // Blue
        else -> "—" to Color.Gray
    }

    StatusBadge(text = label, color = color, modifier = modifier)
}

/**
 * Badge para status de beneficiário
 */
@Composable
fun BeneficiaryStatusBadge(status: String?, modifier: Modifier = Modifier) {
    val (label, color) = when (status) {
        "ACTIVE" -> "Ativo" to Color(0xFF66BB6A) // Green
        "PENDING" -> "Pendente" to Color(0xFFFFA726) // Orange
        "INACTIVE" -> "Inativo" to Color.Gray
        else -> "—" to Color.Gray
    }

    StatusBadge(text = label, color = color, modifier = modifier)
}

/**
 * Badge para status de stock
 */
@Composable
fun StockStatusBadge(
    currentStock: Int,
    minStock: Int,
    modifier: Modifier = Modifier
) {
    val (label, color) = when {
        currentStock <= 0 -> "Esgotado" to Color(0xFFF44336) // Red
        currentStock < minStock -> "Baixo" to Color(0xFFFFA726) // Orange
        else -> "OK" to Color(0xFF66BB6A) // Green
    }

    StatusBadge(text = label, color = color, modifier = modifier)
}

/**
 * Badge para alertas de validade
 */
@Composable
fun ExpiryStatusBadge(
    daysUntilExpiry: Int?,
    modifier: Modifier = Modifier
) {
    val (label, color) = when {
        daysUntilExpiry == null -> "Sem validade" to Color.Gray
        daysUntilExpiry < 0 -> "Expirado" to Color(0xFFF44336) // Red
        daysUntilExpiry <= 7 -> "Expira em $daysUntilExpiry dias" to Color(0xFFF44336) // Red
        daysUntilExpiry <= 30 -> "Expira em $daysUntilExpiry dias" to Color(0xFFFFA726) // Orange
        else -> "Válido" to Color(0xFF66BB6A) // Green
    }

    StatusBadge(text = label, color = color, modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun StatusBadgePreview() {
    LojaSocialTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            Text("Delivery Status:")
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                DeliveryStatusBadge("SCHEDULED")
                DeliveryStatusBadge("DELIVERED")
            }

            Text("Donation Status:")
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                DonationStatusBadge("PENDING")
                DonationStatusBadge("RECEIVED")
                DonationStatusBadge("PROCESSED")
            }

            Text("Beneficiary Status:")
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                BeneficiaryStatusBadge("ACTIVE")
                BeneficiaryStatusBadge("PENDING")
                BeneficiaryStatusBadge("INACTIVE")
            }

            Text("Stock Status:")
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                StockStatusBadge(0, 10)
                StockStatusBadge(5, 10)
                StockStatusBadge(15, 10)
            }

            Text("Expiry Status:")
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                ExpiryStatusBadge(-1)
                ExpiryStatusBadge(5)
                ExpiryStatusBadge(15)
                ExpiryStatusBadge(60)
            }
        }
    }
}
