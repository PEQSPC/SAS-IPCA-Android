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

data class DonorCreateState(
    val donor: Donor = Donor(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DonorCreateViewModel @Inject constructor(
    private val donorRepository: DonorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonorCreateState())
    val uiState: StateFlow<DonorCreateState> = _uiState.asStateFlow()

    fun loadDonor(docId: String?) {
        if (docId == null) return

        donorRepository.getDonorById(docId).onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    result.data?.let { donor ->
                        _uiState.value = _uiState.value.copy(
                            donor = donor,
                            isLoading = false,
                            error = null
                        )
                    }
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

    fun setType(value: String) {
        _uiState.value = _uiState.value.copy(
            donor = _uiState.value.donor.copy(type = value)
        )
    }

    fun setName(value: String) {
        _uiState.value = _uiState.value.copy(
            donor = _uiState.value.donor.copy(name = value)
        )
    }

    fun setEmail(value: String) {
        _uiState.value = _uiState.value.copy(
            donor = _uiState.value.donor.copy(email = value)
        )
    }

    fun setNif(value: String) {
        _uiState.value = _uiState.value.copy(
            donor = _uiState.value.donor.copy(nif = value)
        )
    }

    fun save() {
        val donor = _uiState.value.donor

        // Validation
        if (donor.type.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "Tipo é obrigatório")
            return
        }
        if (donor.name.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "Nome é obrigatório")
            return
        }

        val isUpdate = !donor.docId.isNullOrBlank()

        val flow = if (isUpdate) {
            donorRepository.updateDonor(donor)
        } else {
            donorRepository.createDonor(donor)
        }

        flow.onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSaved = true,
                        isLoading = false,
                        error = null
                    )
                }
                is ResultWrapper.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        isSaved = false,
                        error = null
                    )
                }
                is ResultWrapper.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSaved = false,
                        error = result.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}
