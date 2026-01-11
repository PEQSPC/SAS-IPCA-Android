package com.example.lojasocial.ui.beneficiary

import com.example.lojasocial.models.Beneficiary

data class BeneficiariesListState(
    val items: List<Beneficiary> = emptyList(),
    val search: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)