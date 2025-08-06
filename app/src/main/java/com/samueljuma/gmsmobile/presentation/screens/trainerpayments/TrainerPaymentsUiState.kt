package com.samueljuma.gmsmobile.presentation.screens.trainerpayments

import com.samueljuma.gmsmobile.data.models.TrainerPaymentDto

data class TrainerPaymentsUiState(
    val isLoading: Boolean = false,
    val loadingMessage: String = "",
    val trainerPayments: List<TrainerPaymentDto> = emptyList(),
)
