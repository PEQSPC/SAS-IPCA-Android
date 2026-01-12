package com.example.lojasocial.ui.admin.stock

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.data.repository.DonorRepository
import com.example.lojasocial.data.repository.ItemRepository
import com.example.lojasocial.data.repository.StockLotRepository
import com.example.lojasocial.models.Item
import com.example.lojasocial.models.ResultWrapper
import com.example.lojasocial.models.StockLot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LotWithDonor(
    val lot: StockLot,
    val donorName: String? = null,
    val daysUntilExpiry: Int? = null,
    val status: LotStatus = LotStatus.OK
)

enum class LotStatus {
    OK,
    EXPIRING_SOON, // < 30 days
    EXPIRING_VERY_SOON, // < 7 days
    EXPIRED
}

data class StockLotsState(
    val item: Item? = null,
    val lots: List<LotWithDonor> = emptyList(),
    val searchQuery: String = "",
    val filteredLots: List<LotWithDonor> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class StockLotsViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val stockLotRepository: StockLotRepository,
    private val donorRepository: DonorRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId: String = savedStateHandle.get<String>("itemId") ?: ""

    private val _state = MutableStateFlow(StockLotsState())
    val state: StateFlow<StockLotsState> = _state.asStateFlow()

    init {
        loadItemAndLots()
    }

    fun loadItemAndLots() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Load item info
            itemRepository.getItemById(itemId).collect { result ->
                when (result) {
                    is ResultWrapper.Success -> {
                        _state.update { it.copy(item = result.data) }
                        loadLots()
                    }
                    is ResultWrapper.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Erro ao carregar produto: ${result.message}"
                            )
                        }
                    }
                    is ResultWrapper.Loading -> {}
                }
            }
        }
    }

    private fun loadLots() {
        viewModelScope.launch {
            stockLotRepository.getStockLots(itemId).collect { result ->
                when (result) {
                    is ResultWrapper.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is ResultWrapper.Success -> {
                        val lots = result.data ?: emptyList()
                        Log.d("StockLotsVM", "Lots loaded: ${lots.size}")

                        // Enrich lots with donor info and expiry status
                        val lotsWithDonor = lots.map { lot ->
                            enrichLotWithInfo(lot)
                        }.sortedBy { it.daysUntilExpiry ?: Int.MAX_VALUE } // Sort by closest expiry first

                        _state.update {
                            it.copy(
                                lots = lotsWithDonor,
                                filteredLots = applyFilter(lotsWithDonor, it.searchQuery),
                                isLoading = false
                            )
                        }
                    }
                    is ResultWrapper.Error -> {
                        Log.e("StockLotsVM", "Error loading lots: ${result.message}")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Erro ao carregar lotes: ${result.message}"
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun enrichLotWithInfo(lot: StockLot): LotWithDonor {
        var donorName: String? = null

        // Get donor name
        lot.donorId?.let { donorId ->
            donorRepository.getDonorById(donorId).collect { result ->
                if (result is ResultWrapper.Success) {
                    donorName = result.data?.name
                }
            }
        }

        // Calculate days until expiry
        val now = System.currentTimeMillis()
        var daysUntilExpiry: Int? = null
        var status = LotStatus.OK

        lot.expiryDate?.let { expiryTimestamp ->
            try {
                val expiryMillis = expiryTimestamp.toDate().time
                daysUntilExpiry = ((expiryMillis - now) / (1000 * 60 * 60 * 24)).toInt()

                status = when {
                    daysUntilExpiry!! < 0 -> LotStatus.EXPIRED
                    daysUntilExpiry!! < 7 -> LotStatus.EXPIRING_VERY_SOON
                    daysUntilExpiry!! < 30 -> LotStatus.EXPIRING_SOON
                    else -> LotStatus.OK
                }
            } catch (e: Exception) {
                Log.e("StockLotsVM", "Error parsing date", e)
            }
        }

        return LotWithDonor(
            lot = lot,
            donorName = donorName,
            daysUntilExpiry = daysUntilExpiry,
            status = status
        )
    }

    fun updateSearchQuery(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                filteredLots = applyFilter(it.lots, query)
            )
        }
    }

    private fun applyFilter(lots: List<LotWithDonor>, searchQuery: String): List<LotWithDonor> {
        if (searchQuery.isBlank()) return lots

        return lots.filter { lotWithDonor ->
            lotWithDonor.lot.lot?.contains(searchQuery, ignoreCase = true) == true ||
            lotWithDonor.donorName?.contains(searchQuery, ignoreCase = true) == true
        }
    }
}
