package com.samueljuma.gmsmobile.presentation.screens.expenses

import com.samueljuma.gmsmobile.data.models.CategoryDto
import com.samueljuma.gmsmobile.data.models.ExpenseDto
import com.samueljuma.gmsmobile.domain.models.Category
import com.samueljuma.gmsmobile.domain.models.Expense

data class ExpensesUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String = "",
    val loadingMessage: String = "",
    val expenses: List<ExpenseDto> = emptyList(),
    val expenseCategories: List<Category> = emptyList(),
    val showAddExpenseDialog: Boolean = false,
    val newExpenseDetails: Expense = Expense(),
    val showConfirmDeleteRecordDialog: Boolean = false,
    val selectedRecord: ExpenseDto? = null,
    val showEditPaymentDialog: Boolean = false
)
