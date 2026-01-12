package com.example.lojasocial.ui.admin.deliveries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.data.repository.DeliveryRepository
import com.example.lojasocial.models.Delivery
import com.example.lojasocial.models.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class DeliveriesListState(
    val items: List<Delivery> = emptyList(),
    val search: String = "",
    val statusFilter: String = "Todos", // "Todos", "SCHEDULED", "DELIVERED"
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DeliveriesListViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveriesListState())
    val uiState: StateFlow<DeliveriesListState> = _uiState.asStateFlow()

    fun fetch() {
        deliveryRepository.getDeliveries().onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.value = _uiState.value.copy(
                        items = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is ResultWrapper.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                }
                is ResultWrapper.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setSearch(value: String) {
        _uiState.value = _uiState.value.copy(search = value)
    }

    fun setStatusFilter(value: String) {
        _uiState.value = _uiState.value.copy(statusFilter = value)
    }
}
