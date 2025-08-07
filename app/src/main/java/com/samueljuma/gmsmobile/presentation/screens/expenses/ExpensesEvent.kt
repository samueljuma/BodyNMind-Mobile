package com.samueljuma.gmsmobile.presentation.screens.expenses

sealed class ExpensesEvent{
    data class ShowToastMessage(val message: String): ExpensesEvent()
}