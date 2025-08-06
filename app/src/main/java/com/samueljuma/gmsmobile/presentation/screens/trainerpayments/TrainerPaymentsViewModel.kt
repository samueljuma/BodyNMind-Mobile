package com.samueljuma.gmsmobile.presentation.screens.trainerpayments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.domain.repositories.ExpensesRepository
import com.samueljuma.gmsmobile.utils.sampleTrainerPayments
import com.samueljuma.gmsmobile.utils.toTrainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrainerPaymentsViewModel(
    private val repository: ExpensesRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(TrainerPaymentsUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<TrainerPaymentsEvent>()
    val event = _event.asSharedFlow()

    fun fetchTrainerPayments(){
        _uiState.update { it.copy(isLoading = true, loadingMessage = "Fetching Trainer Payments...")  }
        viewModelScope.launch {

            val result = repository.fetchTrainerPayments()
            when(result){
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        error = result.message,
                        isLoading = false
                    ) }

                    showToast(result.message)
                }
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        trainerPayments = result.data.data,
                        isLoading = false
                    ) }

                    //Fetch Trainers for adding any new payments if needed
                    fetchTrainers()
                }

            }

        }
    }

    private fun fetchTrainers(){
        viewModelScope.launch {
            val result = repository.fetchTrainers("Trainer")
            when(result){
                is NetworkResult.Error -> {
                    showToast("There was an error while fetching trainers: ${result.message}")
                }
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        gymTrainers = result.data.data.map { user -> user.toTrainer() }
                    ) }
                }
            }
        }
    }

    fun updateShowAddPaymentDialog(show: Boolean){
        _uiState.update { it.copy(showAddPaymentDialog = show) }
    }

    fun showToast(message: String){
        viewModelScope.launch {
            _event.emit(TrainerPaymentsEvent.ShowToastMessage(message))
        }

    }


}