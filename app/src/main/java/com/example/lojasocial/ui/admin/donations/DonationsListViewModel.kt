package com.example.lojasocial.ui.admin.donations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.data.repository.DonationRepository
import com.example.lojasocial.models.Donation
import com.example.lojasocial.models.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class DonationsListState(
    val items: List<Donation> = emptyList(),
    val search: String = "",
    val statusFilter: String? = null,  // null = all, "PENDING", "RECEIVED", "PROCESSED"
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DonationsListViewModel @Inject constructor(
    private val donationRepository: DonationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonationsListState())
    val uiState: StateFlow<DonationsListState> = _uiState.asStateFlow()

    fun fetch() {
        donationRepository.getDonations().onEach { result ->
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

    fun setStatusFilter(status: String?) {
        _uiState.value = _uiState.value.copy(statusFilter = status)
    }
}
