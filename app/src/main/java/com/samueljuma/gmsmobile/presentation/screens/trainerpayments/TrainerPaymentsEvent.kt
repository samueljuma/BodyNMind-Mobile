package com.samueljuma.gmsmobile.presentation.screens.trainerpayments

sealed class TrainerPaymentsEvent{
    data class ShowToastMessage(val message: String): TrainerPaymentsEvent()
}
