package com.samueljuma.gmsmobile.presentation.screens.trainerpayments

import com.samueljuma.gmsmobile.data.models.TrainerPaymentDto
import com.samueljuma.gmsmobile.domain.models.Trainer
import com.samueljuma.gmsmobile.domain.models.TrainerPayment

data class TrainerPaymentsUiState(
    val isLoading: Boolean = false,
    val error: String = "",
    val loadingMessage: String = "",
    val gymTrainers: List<Trainer> = emptyList(),
    val trainerPayments: List<TrainerPaymentDto> = emptyList(),
    val showAddPaymentDialog: Boolean = false,
    val newTrainerPayment: TrainerPayment = TrainerPayment(),
    val showConfirmDeleteRecordDialog: Boolean = false,
    val recordToDelete: TrainerPaymentDto? = null
)
