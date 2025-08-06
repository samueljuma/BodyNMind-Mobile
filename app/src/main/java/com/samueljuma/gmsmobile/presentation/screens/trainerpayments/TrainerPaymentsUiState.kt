package com.samueljuma.gmsmobile.presentation.screens.trainerpayments

import com.samueljuma.gmsmobile.data.models.TrainerPaymentDto
import com.samueljuma.gmsmobile.data.models.User
import com.samueljuma.gmsmobile.domain.models.Trainer

data class TrainerPaymentsUiState(
    val isLoading: Boolean = false,
    val error: String = "",
    val loadingMessage: String = "",
    val gymTrainers: List<Trainer> = emptyList(),
    val trainerPayments: List<TrainerPaymentDto> = emptyList(),
)
