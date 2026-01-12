package com.example.lojasocial.ui.admin.donors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.data.repository.DonorRepository
import com.example.lojasocial.models.Donor
import com.example.lojasocial.models.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class DonorsListState(
    val items: List<Donor> = emptyList(),
    val search: String = "",
    val typeFilter: String = "Todos", // "Todos", "COMPANY", "PRIVATE"
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DonorsListViewModel @Inject constructor(
    private val donorRepository: DonorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonorsListState())
    val uiState: StateFlow<DonorsListState> = _uiState.asStateFlow()

    fun fetch() {
        donorRepository.getDonors().onEach { result ->
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

    fun setTypeFilter(value: String) {
        _uiState.value = _uiState.value.copy(typeFilter = value)
    }

    fun deleteDonor(donorId: String) {
        donorRepository.deleteDonor(donorId).onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    // Refresh the list after deletion
                    fetch()
                }
                is ResultWrapper.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is ResultWrapper.Loading -> {
                    // Optional: Show loading state during deletion
                }
            }
        }.launchIn(viewModelScope)
    }
}
