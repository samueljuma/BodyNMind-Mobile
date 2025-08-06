package com.samueljuma.gmsmobile.presentation.screens.trainerpayments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samueljuma.gmsmobile.utils.sampleTrainerPayments
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrainerPaymentsViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(TrainerPaymentsUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<TrainerPaymentsEvent>()
    val event = _event.asSharedFlow()

    fun fetchTrainerPayments(){
        _uiState.update { it.copy(isLoading = true, loadingMessage = "Fetching Trainer Payments...")  }
        viewModelScope.launch {
            delay(800)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    trainerPayments = sampleTrainerPayments,
                    loadingMessage = ""
                )
            }

        }
    }


}