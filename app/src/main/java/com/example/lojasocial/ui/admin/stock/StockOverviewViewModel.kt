package com.example.lojasocial.ui.admin.stock

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.data.repository.ItemRepository
import com.example.lojasocial.data.repository.StockLotRepository
import com.example.lojasocial.models.Item
import com.example.lojasocial.models.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemWithStockInfo(
    val item: Item,
    val activeLots: Int = 0,
    val expiringInDays: Int? = null // null = sem alerta, número = dias até expirar
)

data class StockOverviewState(
    val items: List<ItemWithStockInfo> = emptyList(),
    val filteredItems: List<ItemWithStockInfo> = emptyList(),
    val showOnlyLowStock: Boolean = false,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class StockOverviewViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val stockLotRepository: StockLotRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StockOverviewState())
    val state: StateFlow<StockOverviewState> = _state.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            itemRepository.getItems().collect { result ->
                when (result) {
                    is ResultWrapper.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is ResultWrapper.Success -> {
                        val items = result.data ?: emptyList()
                        Log.d("StockOverviewVM", "Items loaded: ${items.size}")

                        // Load stock info for each item
                        val itemsWithInfo = items.map { item ->
                            getItemWithStockInfo(item)
                        }

                        _state.update {
                            it.copy(
                                items = itemsWithInfo,
                                filteredItems = applyFilters(itemsWithInfo, it.showOnlyLowStock, it.searchQuery),
                                isLoading = false
                            )
                        }
                    }
                    is ResultWrapper.Error -> {
                        Log.e("StockOverviewVM", "Error loading items: ${result.message}")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getItemWithStockInfo(item: Item): ItemWithStockInfo {
        var activeLots = 0
        var closestExpiry: Int? = null

        stockLotRepository.getStockLots(item.docId ?: "").collect { result ->
            if (result is ResultWrapper.Success) {
                val lots = result.data ?: emptyList()
                activeLots = lots.filter { it.remainingQty > 0 }.size

                // Calculate closest expiry date
                val now = System.currentTimeMillis()
                lots.forEach { lot ->
                    lot.expiryDate?.let { expiryTimestamp ->
                        try {
                            val expiryMillis = expiryTimestamp.toDate().time
                            val daysUntilExpiry = ((expiryMillis - now) / (1000 * 60 * 60 * 24)).toInt()

                            if (daysUntilExpiry >= 0 && (closestExpiry == null || daysUntilExpiry < closestExpiry!!)) {
                                closestExpiry = daysUntilExpiry
                            }
                        } catch (e: Exception) {
                            Log.e("StockOverviewVM", "Error parsing date", e)
                        }
                    }
                }
            }
        }

        return ItemWithStockInfo(
            item = item,
            activeLots = activeLots,
            expiringInDays = if (closestExpiry != null && closestExpiry!! < 30) closestExpiry else null
        )
    }

    fun toggleLowStockFilter() {
        _state.update {
            val newShowOnlyLowStock = !it.showOnlyLowStock
            it.copy(
                showOnlyLowStock = newShowOnlyLowStock,
                filteredItems = applyFilters(it.items, newShowOnlyLowStock, it.searchQuery)
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                filteredItems = applyFilters(it.items, it.showOnlyLowStock, query)
            )
        }
    }

    private fun applyFilters(
        items: List<ItemWithStockInfo>,
        showOnlyLowStock: Boolean,
        searchQuery: String
    ): List<ItemWithStockInfo> {
        var filtered = items

        // Filter by low stock
        if (showOnlyLowStock) {
            filtered = filtered.filter { itemInfo ->
                val current = itemInfo.item.stockCurrent ?: 0
                val min = itemInfo.item.minStock ?: 0
                current < min
            }
        }

        // Filter by search query
        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter { itemInfo ->
                val item = itemInfo.item
                item.name?.contains(searchQuery, ignoreCase = true) == true ||
                item.sku?.contains(searchQuery, ignoreCase = true) == true ||
                item.eans?.contains(searchQuery, ignoreCase = true) == true
            }
        }

        return filtered
    }
}
