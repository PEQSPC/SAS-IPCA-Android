package com.example.lojasocial.ui.admin.stock

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.data.repository.ItemRepository
import com.example.lojasocial.data.repository.StockMoveRepository
import com.example.lojasocial.models.ResultWrapper
import com.example.lojasocial.models.StockMove
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MoveWithItemName(
    val move: StockMove,
    val itemName: String? = null
)

data class StockMovesState(
    val moves: List<MoveWithItemName> = emptyList(),
    val filteredMoves: List<MoveWithItemName> = emptyList(),
    val selectedType: MoveType = MoveType.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class MoveType {
    ALL, IN, OUT
}

@HiltViewModel
class StockMovesViewModel @Inject constructor(
    private val stockMoveRepository: StockMoveRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StockMovesState())
    val state: StateFlow<StockMovesState> = _state.asStateFlow()

    init {
        loadMoves()
    }

    fun loadMoves() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            stockMoveRepository.getStockMoves().collect { result ->
                when (result) {
                    is ResultWrapper.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is ResultWrapper.Success -> {
                        val moves = result.data ?: emptyList()
                        Log.d("StockMovesVM", "Moves loaded: ${moves.size}")

                        // Enrich moves with item names
                        val movesWithNames = moves.map { move ->
                            enrichMoveWithItemName(move)
                        }.sortedByDescending { it.move.createdAt?.seconds ?: 0 } // Most recent first

                        _state.update {
                            it.copy(
                                moves = movesWithNames,
                                filteredMoves = applyFilters(movesWithNames, it.selectedType, it.searchQuery),
                                isLoading = false
                            )
                        }
                    }
                    is ResultWrapper.Error -> {
                        Log.e("StockMovesVM", "Error loading moves: ${result.message}")
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

    private suspend fun enrichMoveWithItemName(move: StockMove): MoveWithItemName {
        var itemName: String? = null

        move.itemId?.let { itemId ->
            itemRepository.getItemById(itemId).collect { result ->
                if (result is ResultWrapper.Success) {
                    itemName = result.data?.name
                }
            }
        }

        return MoveWithItemName(
            move = move,
            itemName = itemName
        )
    }

    fun selectType(type: MoveType) {
        _state.update {
            it.copy(
                selectedType = type,
                filteredMoves = applyFilters(it.moves, type, it.searchQuery)
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                filteredMoves = applyFilters(it.moves, it.selectedType, query)
            )
        }
    }

    private fun applyFilters(
        moves: List<MoveWithItemName>,
        type: MoveType,
        searchQuery: String
    ): List<MoveWithItemName> {
        var filtered = moves

        // Filter by type
        if (type != MoveType.ALL) {
            filtered = filtered.filter { moveWithName ->
                when (type) {
                    MoveType.IN -> moveWithName.move.type == "IN"
                    MoveType.OUT -> moveWithName.move.type == "OUT"
                    else -> true
                }
            }
        }

        // Filter by search query
        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter { moveWithName ->
                moveWithName.itemName?.contains(searchQuery, ignoreCase = true) == true ||
                moveWithName.move.lotNumber?.contains(searchQuery, ignoreCase = true) == true
            }
        }

        return filtered
    }
}
