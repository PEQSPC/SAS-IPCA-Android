package com.example.lojasocial.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.core.auth.AuthStateHolder
import com.example.lojasocial.data.repository.BeneficiaryRepository
import com.example.lojasocial.data.repository.DeliveryRepository
import com.example.lojasocial.data.repository.DonationRepository
import com.example.lojasocial.data.repository.ItemRepository
import com.example.lojasocial.data.repository.StockLotRepository
import com.example.lojasocial.models.ResultWrapper
import com.example.lojasocial.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ---------- MODELS ----------

data class DashboardStats(
    val totalItems: Int = 0,
    val itemsInAlert: Int = 0, // items with stock < minStock
    val itemsExpiringSoon: Int = 0, // items expiring in < 30 days
    val totalDonations: Int = 0,
    val totalBeneficiaries: Int = 0,
    val totalDeliveries: Int = 0
) {
    fun hasAlerts(): Boolean = itemsInAlert > 0 || itemsExpiringSoon > 0
}

data class AdminStartState(
    val user: User? = null,
    val dashboardStats: DashboardStats? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// ---------- VIEWMODEL ----------

@HiltViewModel
class AdminStartViewModel @Inject constructor(
    private val authStateHolder: AuthStateHolder,
    private val itemRepository: ItemRepository,
    private val stockLotRepository: StockLotRepository,
    private val donationRepository: DonationRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val deliveryRepository: DeliveryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminStartState())
    val uiState: StateFlow<AdminStartState> = _uiState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authStateHolder.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(user = user)
            }
        }
    }

    fun loadDashboardStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                var totalItems = 0
                var itemsInAlert = 0
                var itemsExpiringSoon = 0

                // Load items and check alerts
                itemRepository.getItems().collect { result ->
                    when (result) {
                        is ResultWrapper.Success -> {
                            val items = result.data ?: emptyList()
                            totalItems = items.size

                            // Count items with low stock
                            itemsInAlert = items.count { item ->
                                val current = item.stockCurrent ?: 0
                                val min = item.minStock ?: 0
                                current < min
                            }

                            // Check expiring items (items with lots expiring in < 30 days)
                            items.forEach { item ->
                                stockLotRepository.getStockLots(item.docId ?: "").collect { lotsResult ->
                                    if (lotsResult is ResultWrapper.Success) {
                                        val lots = lotsResult.data ?: emptyList()
                                        val hasExpiringLot = lots.any { lot ->
                                            lot.expiryDate?.let { expiryTimestamp ->
                                                try {
                                                    val expiryMillis = expiryTimestamp.toDate().time
                                                    val now = System.currentTimeMillis()
                                                    val daysUntilExpiry = ((expiryMillis - now) / (1000 * 60 * 60 * 24)).toInt()

                                                    daysUntilExpiry in 0..30
                                                } catch (e: Exception) {
                                                    false
                                                }
                                            } ?: false
                                        }
                                        if (hasExpiringLot) {
                                            itemsExpiringSoon++
                                        }
                                    }
                                }
                            }
                        }
                        else -> {}
                    }
                }

                // Load donations count
                var totalDonations = 0
                donationRepository.getDonations().collect { result ->
                    if (result is ResultWrapper.Success) {
                        totalDonations = result.data?.size ?: 0
                    }
                }

                // Load beneficiaries count
                var totalBeneficiaries = 0
                beneficiaryRepository.getBeneficiaries().collect { result ->
                    if (result is ResultWrapper.Success) {
                        totalBeneficiaries = result.data?.size ?: 0
                    }
                }

                // Load deliveries count
                var totalDeliveries = 0
                deliveryRepository.getDeliveries().collect { result ->
                    if (result is ResultWrapper.Success) {
                        totalDeliveries = result.data?.size ?: 0
                    }
                }

                _uiState.value = _uiState.value.copy(
                    dashboardStats = DashboardStats(
                        totalItems = totalItems,
                        itemsInAlert = itemsInAlert,
                        itemsExpiringSoon = itemsExpiringSoon,
                        totalDonations = totalDonations,
                        totalBeneficiaries = totalBeneficiaries,
                        totalDeliveries = totalDeliveries
                    ),
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar estatísticas"
                )
            }
        }
    }

    fun logout() {
        authStateHolder.signOut()
    }
}
