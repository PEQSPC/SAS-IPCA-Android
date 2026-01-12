package com.example.lojasocial.ui.admin.donations

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.data.repository.DonationRepository
import com.example.lojasocial.data.repository.DonorRepository
import com.example.lojasocial.data.repository.StockLotRepository
import com.example.lojasocial.data.repository.StockMoveRepository
import com.example.lojasocial.models.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonationLineUI(
    val itemId: String = "",
    val itemName: String = "",
    val quantityText: String = "",
    val expiryDateText: String = "" // dd/MM/yyyy
)

data class DonationCreateState(
    val donation: Donation = Donation(),
    val donorIdText: String = "",
    val donorNameText: String = "",
    val dateText: String = "", // dd/MM/yyyy
    val notesText: String = "",
    val lines: List<DonationLineUI> = emptyList(),
    val availableDonors: List<Donor> = emptyList(),
    val availableItems: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DonationCreateViewModel @Inject constructor(
    private val donationRepository: DonationRepository,
    private val donorRepository: DonorRepository,
    private val stockLotRepository: StockLotRepository,
    private val stockMoveRepository: StockMoveRepository,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonationCreateState())
    val uiState: StateFlow<DonationCreateState> = _uiState.asStateFlow()

    init {
        loadDonors()
        loadItems()
    }

    private fun loadDonors() {
        donorRepository.getDonors().onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.value = _uiState.value.copy(
                        availableDonors = result.data ?: emptyList()
                    )
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun loadItems() {
        viewModelScope.launch {
            try {
                // Load items from Firestore
                val snapshot = db.collection("items").get().await()
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Item::class.java)?.apply {
                        docId = doc.id
                    }
                }
                _uiState.value = _uiState.value.copy(availableItems = items)
            } catch (e: Exception) {
                Log.e("DonationCreateVM", "Error loading items", e)
            }
        }
    }

    fun setDonorId(donorId: String) {
        val donor = _uiState.value.availableDonors.find { it.docId == donorId }
        _uiState.value = _uiState.value.copy(
            donorIdText = donorId,
            donorNameText = donor?.name ?: ""
        )
    }

    fun setDateText(value: String) {
        _uiState.value = _uiState.value.copy(dateText = value)
    }

    fun setNotesText(value: String) {
        _uiState.value = _uiState.value.copy(notesText = value)
    }

    fun addLine() {
        val currentLines = _uiState.value.lines.toMutableList()
        currentLines.add(DonationLineUI())
        _uiState.value = _uiState.value.copy(lines = currentLines)
    }

    fun removeLine(index: Int) {
        val currentLines = _uiState.value.lines.toMutableList()
        if (index in currentLines.indices) {
            currentLines.removeAt(index)
            _uiState.value = _uiState.value.copy(lines = currentLines)
        }
    }

    fun updateLine(index: Int, line: DonationLineUI) {
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
                if (_uiState.value.donorIdText.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Doador é obrigatório"
                    )
                    return@launch
                }

                if (_uiState.value.dateText.isBlank()) {
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
                val dateParts = _uiState.value.dateText.split("/")
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
                val donationTimestamp = Timestamp(calendar.time)

                // Create donation
                val donation = Donation(
                    donorId = _uiState.value.donorIdText,
                    donorName = _uiState.value.donorNameText,
                    date = donationTimestamp,
                    notes = _uiState.value.notesText.ifBlank { null }
                )

                var donationId: String? = null

                donationRepository.createDonation(donation).collect { result ->
                    when (result) {
                        is ResultWrapper.Success -> {
                            donationId = result.data
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

                if (donationId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Erro ao criar doação"
                    )
                    return@launch
                }

                // Create donation lines and stock lots
                _uiState.value.lines.forEach { lineUI ->
                    // Validate line
                    if (lineUI.itemId.isBlank() || lineUI.quantityText.isBlank()) {
                        return@forEach
                    }

                    val quantity = lineUI.quantityText.toIntOrNull() ?: 0
                    if (quantity <= 0) return@forEach

                    // Parse expiry date (optional)
                    var expiryTimestamp: Timestamp? = null
                    if (lineUI.expiryDateText.isNotBlank()) {
                        val expiryParts = lineUI.expiryDateText.split("/")
                        if (expiryParts.size == 3) {
                            val expiryDay = expiryParts[0].toIntOrNull() ?: 1
                            val expiryMonth = expiryParts[1].toIntOrNull() ?: 1
                            val expiryYear = expiryParts[2].toIntOrNull() ?: 2025
                            val expiryCal = java.util.Calendar.getInstance().apply {
                                set(expiryYear, expiryMonth - 1, expiryDay, 0, 0, 0)
                                set(java.util.Calendar.MILLISECOND, 0)
                            }
                            expiryTimestamp = Timestamp(expiryCal.time)
                        }
                    }

                    // Create donation line
                    val donationLine = DonationLine(
                        itemId = lineUI.itemId,
                        itemName = lineUI.itemName,
                        quantity = quantity,
                        expiryDate = expiryTimestamp
                    )

                    donationRepository.addDonationLine(donationId!!, donationLine).collect { result ->
                        when (result) {
                            is ResultWrapper.Success -> {
                                Log.d("DonationCreateVM", "Donation line created")
                            }
                            is ResultWrapper.Error -> {
                                Log.e("DonationCreateVM", "Error creating donation line: ${result.message}")
                            }
                            is ResultWrapper.Loading -> {}
                        }
                    }

                    // Create stock lot
                    val lotNumber = "LOT-${System.currentTimeMillis()}"
                    val stockLot = StockLot(
                        lot = lotNumber,
                        quantity = quantity,
                        remainingQty = quantity,
                        expiryDate = expiryTimestamp,
                        donorId = _uiState.value.donorIdText
                    )

                    stockLotRepository.createStockLot(lineUI.itemId, stockLot).collect { result ->
                        when (result) {
                            is ResultWrapper.Success -> {
                                Log.d("DonationCreateVM", "Stock lot created")
                            }
                            is ResultWrapper.Error -> {
                                Log.e("DonationCreateVM", "Error creating stock lot: ${result.message}")
                            }
                            is ResultWrapper.Loading -> {}
                        }
                    }

                    // Create stock move (IN)
                    val stockMove = StockMove(
                        itemId = lineUI.itemId,
                        lotId = lotNumber,
                        type = "IN",
                        quantity = quantity,
                        createdAt = Timestamp.now()
                    )

                    stockMoveRepository.createStockMove(stockMove).collect { result ->
                        when (result) {
                            is ResultWrapper.Success -> {
                                Log.d("DonationCreateVM", "Stock move created")
                            }
                            is ResultWrapper.Error -> {
                                Log.e("DonationCreateVM", "Error creating stock move: ${result.message}")
                            }
                            is ResultWrapper.Loading -> {}
                        }
                    }

                    // Update item stockCurrent
                    try {
                        val itemRef = db.collection("items").document(lineUI.itemId)
                        db.runTransaction { transaction ->
                            val itemSnapshot = transaction.get(itemRef)
                            val currentStock = itemSnapshot.getLong("stockCurrent") ?: 0
                            transaction.update(itemRef, "stockCurrent", currentStock + quantity)
                        }.await()
                    } catch (e: Exception) {
                        Log.e("DonationCreateVM", "Error updating stock", e)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSaved = true,
                    error = null
                )

            } catch (e: Exception) {
                Log.e("DonationCreateVM", "Error saving donation", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao guardar doação: ${e.message}"
                )
            }
        }
    }
}
