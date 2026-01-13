package com.example.lojasocial.ui.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DonationStatusBadge(
    status: String?,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status?.uppercase()) {
        "PENDING" -> Triple(
            Color(0xFFFFA726).copy(alpha = 0.2f),
            Color(0xFFF57C00),
            "Triagem"
        )
        "RECEIVED" -> Triple(
            Color(0xFF66BB6A).copy(alpha = 0.2f),
            Color(0xFF2E7D32),
            "Recebida"
        )
        "PROCESSED" -> Triple(
            Color(0xFF42A5F5).copy(alpha = 0.2f),
            Color(0xFF1976D2),
            "Processada"
        )
        else -> Triple(
            Color(0xFFBDBDBD).copy(alpha = 0.2f),
            Color(0xFF616161),
            "Desconhecido"
        )
    }

    Text(
        text = label,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
}

@Preview(showBackground = true)
@Composable
fun DonationStatusBadgePreview() {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        DonationStatusBadge(status = "PENDING")
        DonationStatusBadge(status = "RECEIVED")
        DonationStatusBadge(status = "PROCESSED")
        DonationStatusBadge(status = null)
    }
}
