package com.samueljuma.gmsmobile.presentation.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samueljuma.gmsmobile.data.models.CreateExpenseDto
import com.samueljuma.gmsmobile.data.models.ExpenseDto
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.domain.models.Expense
import com.samueljuma.gmsmobile.domain.repositories.ExpensesRepository
import com.samueljuma.gmsmobile.utils.toCategory
import com.samueljuma.gmsmobile.utils.toCreateExpenseDto
import com.samueljuma.gmsmobile.utils.toExpense
import com.samueljuma.gmsmobile.utils.validateAmount
import com.samueljuma.gmsmobile.utils.validateCategory
import com.samueljuma.gmsmobile.utils.validateName
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpensesViewModel(
    private val repository: ExpensesRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState: StateFlow<ExpensesUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ExpensesEvent>()
    val event = _event.asSharedFlow()

    init {
        fetchExpenses()
    }

    fun fetchExpenses(isRefresh: Boolean = false){
        _uiState.update {
            it.copy(
                isLoading = if(!isRefresh) true else it.isLoading,
                isRefreshing = if(isRefresh) true else it.isRefreshing,
                loadingMessage = if(isRefresh) "Refreshing Expenses..." else "Fetching Expenses..."
            )
        }
        viewModelScope.launch {
            val result = repository.fetchExpenses()
            when(result){
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            error = result.message,
                            isLoading = false,
                            isRefreshing = false,
                            loadingMessage = ""
                        )
                    }
                    showToast(result.message)
                }
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            expenses = result.data.data,
                            isLoading = false,
                            isRefreshing = false,
                            loadingMessage = ""
                        )
                    }

                    // fetch ExpenseCategories
                    fetchExpenseCategories()
                }

            }
        }
    }

    private fun fetchExpenseCategories(){
        viewModelScope.launch {
            val result = repository.fetchExpenseCategories()
            when(result){
                is NetworkResult.Error -> {
                    showToast(result.message)
                }
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(expenseCategories = result.data.data.map {
                        categoryDto-> categoryDto.toCategory() }) }
                }
            }
        }
    }

    fun createExpense(request: CreateExpenseDto){
        _uiState.update { it.copy(isLoading = true, loadingMessage = "Adding Expense...")  }
        viewModelScope.launch {
            val result = repository.createExpense(request)
            when(result){
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        error = result.message,
                        isLoading = false,
                        loadingMessage = ""
                    ) }
                }
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        loadingMessage = ""
                    ) }
                    showToast("Expense added successfully")

                    fetchExpenses(isRefresh = true)

                    updateShowAddExpenseDialog(false)
                }
            }
        }
    }
    fun deleteExpense(){
        if(uiState.value.selectedRecord == null){
            showToast("No record selected")
            return
        }
        _uiState.update { it.copy(isLoading = true, loadingMessage = "Deleting Record...") }
        viewModelScope.launch {
            val result = repository.deleteExpense(uiState.value.selectedRecord!!.id)
            when(result){
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        error = result.message,
                        isLoading = false,
                        loadingMessage = ""
                    ) }
                }
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        loadingMessage = ""
                    ) }
                    showToast("Record deleted successfully")
                    updateShowConfirmDeleteDialog(false)
                    fetchExpenses(isRefresh = true)

                }
            }
        }
    }
    fun updateExpense(request: CreateExpenseDto){
        if(uiState.value.selectedRecord == null){
            showToast("No record selected")
            return
        }
        _uiState.update { it.copy(isLoading = true, loadingMessage = "Updating Record...") }
        viewModelScope.launch {
            val result = repository.updateExpense(
                recordID = uiState.value.selectedRecord!!.id,
                request = request
            )
            when(result){
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        error = result.message,
                        isLoading = false,
                        loadingMessage = ""
                    ) }
                    showToast(result.message)
                }
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        loadingMessage = ""
                    ) }
                    showToast("Record updated successfully")
                    updateShowEditPaymentDialog(false)
                    fetchExpenses(isRefresh = true)
                }
            }

        }
    }



    fun onSaveExpenseRecord(){
        val newExpense = uiState.value.newExpenseDetails
        val validatedExpense = newExpense.copy(
            nameError = newExpense.name.validateName(),
            categoryError = newExpense.category.name.validateCategory(),
            amountError = newExpense.amount.validateAmount(),
        )

        _uiState.update { it.copy(newExpenseDetails = validatedExpense) }

        if(validatedExpense.isValid){
            when{
                validatedExpense.nameError != null -> {
                    showToast(validatedExpense.nameError)
                    return
                }
                validatedExpense.categoryError != null -> {
                    showToast(validatedExpense.categoryError)
                    return
                }
                validatedExpense.amountError != null -> {
                    showToast(validatedExpense.amountError)
                    return
                }
            }
        }else{
            createExpense(request = newExpense.toCreateExpenseDto())
        }
    }

    fun onUpdateRecord(){
        val newExpense = uiState.value.newExpenseDetails
        val validatedExpense = newExpense.copy(
            nameError = newExpense.name.validateName(),
            categoryError = newExpense.category.name.validateCategory(),
            amountError = newExpense.amount.validateAmount(),
        )

        _uiState.update { it.copy(newExpenseDetails = validatedExpense) }

        if(validatedExpense.isValid){
            when{
                validatedExpense.nameError != null -> {
                    showToast(validatedExpense.nameError)
                    return
                }
                validatedExpense.categoryError != null -> {
                    showToast(validatedExpense.categoryError)
                    return
                }
                validatedExpense.amountError != null -> {
                    showToast(validatedExpense.amountError)
                    return
                }
            }
        }else{
            updateExpense(request = newExpense.toCreateExpenseDto())
        }
    }

    fun updateShowAddExpenseDialog(show: Boolean){
        _uiState.update { it.copy(showAddExpenseDialog = show) }
        if(!show){
            _uiState.update { it.copy(newExpenseDetails = Expense()) }
        }
    }
    fun updateShowEditPaymentDialog(show: Boolean, record: ExpenseDto? = null){
        _uiState.update {
            it.copy(
                showEditPaymentDialog = show,
                selectedRecord = record,
                newExpenseDetails = record?.toExpense() ?: Expense()
            )
        }
        if(!show){
            _uiState.update { it.copy(newExpenseDetails = Expense()) }
        }
    }
    fun updateShowConfirmDeleteDialog(show: Boolean, record: ExpenseDto? = null){
        _uiState.update { it.copy(showConfirmDeleteRecordDialog = show, selectedRecord = record) }
    }


    fun showToast(message: String){
        viewModelScope.launch {
            _event.emit(ExpensesEvent.ShowToastMessage(message))
        }
    }
}