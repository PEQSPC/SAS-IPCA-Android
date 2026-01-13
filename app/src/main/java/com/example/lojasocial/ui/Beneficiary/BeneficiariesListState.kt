package com.example.lojasocial.ui.beneficiary

import com.example.lojasocial.models.Beneficiary

data class BeneficiariesListState(
    val items: List<Beneficiary> = emptyList(),
    val search: String = "",
    val statusFilter: String? = null,  // null = all, "ACTIVE", "PENDING", "INACTIVE"
    val isLoading: Boolean = false,
    val error: String? = null
)