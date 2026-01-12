package com.example.lojasocial.ui.admin.deliveries

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.data.repository.DeliveryRepository
import com.example.lojasocial.data.repository.StockLotRepository
import com.example.lojasocial.data.repository.StockMoveRepository
import com.example.lojasocial.models.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class DeliveryLineUI(
    val itemId: String = "",
    val itemName: String = "",
    val quantityText: String = ""
)

data class DeliveryCreateState(
    val beneficiaryIdText: String = "",
    val beneficiaryNameText: String = "",
    val scheduledAtText: String = "", // dd/MM/yyyy
    val lines: List<DeliveryLineUI> = emptyList(),
    val availableBeneficiaries: List<Beneficiary> = emptyList(),
    val availableItems: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DeliveryCreateViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository,
    private val stockLotRepository: StockLotRepository,
    private val stockMoveRepository: StockMoveRepository,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveryCreateState())
    val uiState: StateFlow<DeliveryCreateState> = _uiState.asStateFlow()

    init {
        loadBeneficiaries()
        loadItems()
    }

    private fun loadBeneficiaries() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("beneficiaries").get().await()
                val beneficiaries = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Beneficiary::class.java)?.apply {
                        docId = doc.id
                    }
                }
                _uiState.value = _uiState.value.copy(availableBeneficiaries = beneficiaries)
            } catch (e: Exception) {
                Log.e("DeliveryCreateVM", "Error loading beneficiaries", e)
            }
        }
    }

    private fun loadItems() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("items").get().await()
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Item::class.java)?.apply {
                        docId = doc.id
                    }
                }
                _uiState.value = _uiState.value.copy(availableItems = items)
            } catch (e: Exception) {
                Log.e("DeliveryCreateVM", "Error loading items", e)
            }
        }
    }

    fun setBeneficiaryId(beneficiaryId: String) {
        val beneficiary = _uiState.value.availableBeneficiaries.find { it.docId == beneficiaryId }
        _uiState.value = _uiState.value.copy(
            beneficiaryIdText = beneficiaryId,
            beneficiaryNameText = beneficiary?.nome ?: ""
        )
    }

    fun setScheduledAtText(value: String) {
        _uiState.value = _uiState.value.copy(scheduledAtText = value)
    }

    fun addLine() {
        val currentLines = _uiState.value.lines.toMutableList()
        currentLines.add(DeliveryLineUI())
        _uiState.value = _uiState.value.copy(lines = currentLines)
    }

    fun removeLine(index: Int) {
        val currentLines = _uiState.value.lines.toMutableList()
        if (index in currentLines.indices) {
            currentLines.removeAt(index)
            _uiState.value = _uiState.value.copy(lines = currentLines)
        }
    }

    fun updateLine(index: Int, line: DeliveryLineUI) {
        val currentLines = _uiState.value.lines.toMutableList()
        if (index in currentLines.indices) {
            currentLines[index] = line
            _uiState.value = _uiState.value.copy(lines = currentLines)
        }
    }

    fun save() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Validation
                if (_uiState.value.beneficiaryIdText.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Beneficiário é obrigatório"
                    )
                    return@launch
                }

                if (_uiState.value.scheduledAtText.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Data é obrigatória"
                    )
                    return@launch
                }

                if (_uiState.value.lines.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Adicione pelo menos um produto"
                    )
                    return@launch
                }

                // Parse date (dd/MM/yyyy to Timestamp)
                val dateParts = _uiState.value.scheduledAtText.split("/")
                if (dateParts.size != 3) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Data inválida (use dd/MM/yyyy)"
                    )
                    return@launch
                }

                val day = dateParts[0].toIntOrNull() ?: 1
                val month = dateParts[1].toIntOrNull() ?: 1
                val year = dateParts[2].toIntOrNull() ?: 2025

                val calendar = java.util.Calendar.getInstance().apply {
                    set(year, month - 1, day, 0, 0, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                val scheduledTimestamp = Timestamp(calendar.time)

                // Validate stock availability for all lines BEFORE creating delivery
                val stockValidation = mutableMapOf<String, Int>() // itemId -> available quantity
                for (lineUI in _uiState.value.lines) {
                    if (lineUI.itemId.isBlank() || lineUI.quantityText.isBlank()) continue

                    val requestedQty = lineUI.quantityText.toIntOrNull() ?: 0
                    if (requestedQty <= 0) continue

                    // Get stock lots for this item (ordered by expiry date - FIFO)
                    val lotsResult = stockLotRepository.getStockLots(lineUI.itemId).first()
                    val lots = when (lotsResult) {
                        is ResultWrapper.Success -> lotsResult.data ?: emptyList()
                        else -> emptyList()
                    }

                    // Sort by expiry date (FIFO - oldest first, null last)
                    val sortedLots = lots.sortedWith(compareBy(
                        nullsLast()
                    ) { it.expiryDate })

                    val availableQty = sortedLots.sumOf { it.remainingQty }
                    stockValidation[lineUI.itemId] = availableQty

                    if (availableQty < requestedQty) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Stock insuficiente para ${lineUI.itemName}: disponível $availableQty, pedido $requestedQty"
                        )
                        return@launch
                    }
                }

                // Create delivery
                val delivery = Delivery(
                    beneficiaryId = _uiState.value.beneficiaryIdText,
                    beneficiaryName = _uiState.value.beneficiaryNameText,
                    status = "SCHEDULED",
                    scheduledAt = scheduledTimestamp
                )

                var deliveryId: String? = null

                deliveryRepository.createDelivery(delivery).collect { result ->
                    when (result) {
                        is ResultWrapper.Success -> {
                            deliveryId = result.data
                        }
                        is ResultWrapper.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                        is ResultWrapper.Loading -> {}
                    }
                }

                if (deliveryId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Erro ao criar entrega"
                    )
                    return@launch
                }

                // Create delivery lines and consume stock (FIFO)
                for (lineUI in _uiState.value.lines) {
                    if (lineUI.itemId.isBlank() || lineUI.quantityText.isBlank()) continue

                    val requestedQty = lineUI.quantityText.toIntOrNull() ?: 0
                    if (requestedQty <= 0) continue

                    // Get stock lots (sorted by expiry date - FIFO)
                    val lotsResult = stockLotRepository.getStockLots(lineUI.itemId).first()
                    val lots = when (lotsResult) {
                        is ResultWrapper.Success -> lotsResult.data ?: emptyList()
                        else -> emptyList()
                    }

                    val sortedLots = lots.sortedWith(compareBy(
                        nullsLast()
                    ) { it.expiryDate }).filter { it.remainingQty > 0 }

                    // Consume stock using FIFO
                    var remainingToConsume = requestedQty
                    val consumedLots = mutableListOf<Pair<String, Int>>() // lotId, quantity

                    for (lot in sortedLots) {
                        if (remainingToConsume <= 0) break

                        val qtyToConsume = minOf(remainingToConsume, lot.remainingQty)
                        consumedLots.add(Pair(lot.lot ?: "", qtyToConsume))

                        // Update stock lot remainingQty
                        stockLotRepository.consumeStock(lineUI.itemId, lot.docId ?: "", qtyToConsume).collect { result ->
                            when (result) {
                                is ResultWrapper.Success -> {
                                    Log.d("DeliveryCreateVM", "Stock lot consumed: ${lot.lot}, qty: $qtyToConsume")
                                }
                                is ResultWrapper.Error -> {
                                    Log.e("DeliveryCreateVM", "Error consuming stock lot: ${result.message}")
                                }
                                is ResultWrapper.Loading -> {}
                            }
                        }

                        remainingToConsume -= qtyToConsume
                    }

                    // Create delivery lines (one per consumed lot for traceability)
                    for ((lotId, qty) in consumedLots) {
                        val deliveryLine = DeliveryLine(
                            itemId = lineUI.itemId,
                            lotId = lotId,
                            quantity = qty
                        )

                        deliveryRepository.addDeliveryLine(deliveryId!!, deliveryLine).collect { result ->
                            when (result) {
                                is ResultWrapper.Success -> {
                                    Log.d("DeliveryCreateVM", "Delivery line created")
                                }
                                is ResultWrapper.Error -> {
                                    Log.e("DeliveryCreateVM", "Error creating delivery line: ${result.message}")
                                }
                                is ResultWrapper.Loading -> {}
                            }
                        }

                        // Create stock move (OUT)
                        val stockMove = StockMove(
                            itemId = lineUI.itemId,
                            lotId = lotId,
                            type = "OUT",
                            quantity = qty,
                            createdAt = Timestamp.now()
                        )

                        stockMoveRepository.createStockMove(stockMove).collect { result ->
                            when (result) {
                                is ResultWrapper.Success -> {
                                    Log.d("DeliveryCreateVM", "Stock move created")
                                }
                                is ResultWrapper.Error -> {
                                    Log.e("DeliveryCreateVM", "Error creating stock move: ${result.message}")
                                }
                                is ResultWrapper.Loading -> {}
                            }
                        }
                    }

                    // Update item stockCurrent
                    try {
                        val itemRef = db.collection("items").document(lineUI.itemId)
                        db.runTransaction { transaction ->
                            val itemSnapshot = transaction.get(itemRef)
                            val currentStock = itemSnapshot.getLong("stockCurrent") ?: 0
                            transaction.update(itemRef, "stockCurrent", currentStock - requestedQty)
                        }.await()
                    } catch (e: Exception) {
                        Log.e("DeliveryCreateVM", "Error updating stock", e)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSaved = true,
                    error = null
                )

            } catch (e: Exception) {
                Log.e("DeliveryCreateVM", "Error saving delivery", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao guardar entrega: ${e.message}"
                )
            }
        }
    }
}
