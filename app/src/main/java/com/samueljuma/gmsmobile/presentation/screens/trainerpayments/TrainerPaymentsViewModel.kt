package com.samueljuma.gmsmobile.presentation.screens.trainerpayments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samueljuma.gmsmobile.data.models.CreateTrainerPaymentDto
import com.samueljuma.gmsmobile.data.models.TrainerPaymentDto
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.domain.models.Trainer
import com.samueljuma.gmsmobile.domain.models.TrainerPayment
import com.samueljuma.gmsmobile.domain.repositories.ExpensesRepository
import com.samueljuma.gmsmobile.domain.toCreateTrainerPaymentDto
import com.samueljuma.gmsmobile.utils.toTrainer
import com.samueljuma.gmsmobile.utils.toTrainerPayment
import com.samueljuma.gmsmobile.utils.validateAmount
import com.samueljuma.gmsmobile.utils.validateTrainer
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

    fun fetchTrainerPayments(isRefresh: Boolean = false){
        _uiState.update { it.copy(
            isLoading = if(!isRefresh) true else it.isLoading,
            isRefreshing = if(isRefresh) true else it.isRefreshing,
            loadingMessage = if(isRefresh) "Refreshing Trainer Payments..." else "Fetching Trainer Payments..."
        ) }
        viewModelScope.launch {

            val result = repository.fetchTrainerPayments()
            when(result){
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        error = result.message,
                        isLoading = false,
                        isRefreshing = false,
                        loadingMessage = ""
                    ) }

                    showToast(result.message)
                }
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        trainerPayments = result.data.data,
                        isLoading = false,
                        isRefreshing = false,
                        loadingMessage = ""
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

    fun createTrainerPayment(request: CreateTrainerPaymentDto){
        _uiState.update { it.copy(isLoading = true, loadingMessage = "Adding Trainer Payment...")  }
        viewModelScope.launch {
            val result = repository.createTrainerPayment(request)
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
                    showToast("Trainer Payment added successfully")

                    fetchTrainerPayments(isRefresh = true)

                    //Close dialog
                    updateShowAddPaymentDialog(false)
                }
            }
        }
    }



    fun updateShowAddPaymentDialog(show: Boolean){
        _uiState.update { it.copy(showAddPaymentDialog = show) }
        if(!show){
            _uiState.update { it.copy(newTrainerPaymentDetails = TrainerPayment()) }
        }
    }

    fun onSavePaymentRecord() {
        val newPayment = uiState.value.newTrainerPaymentDetails
        val validatedPayment = newPayment.copy(
            trainerError = newPayment.trainer.fullName.validateTrainer(),
            amountError = newPayment.amount.validateAmount()
        )

        // Update UI state with validated payment
        _uiState.update { it.copy(newTrainerPaymentDetails = validatedPayment) }

        if (!validatedPayment.isValid) {
            when {
                validatedPayment.trainerError != null -> {
                    showToast(validatedPayment.trainerError)
                    return
                }
                validatedPayment.amountError != null -> {
                    showToast(validatedPayment.amountError)
                    return
                }
            }
        } else {
            createTrainerPayment(newPayment.toCreateTrainerPaymentDto())

        }
    }


    fun showToast(message: String){
        viewModelScope.launch {
            _event.emit(TrainerPaymentsEvent.ShowToastMessage(message))
        }

    }

    fun updateNewTrainerPayment(field: String, value: String) {
        _uiState.update { currentState ->
            val currentPayment = currentState.newTrainerPaymentDetails

            val updatedPayment = when (field) {
                "trainer" -> currentPayment.copy(
                    trainer = currentState.gymTrainers.find { it.fullName == value } ?: Trainer(),
                    trainerError = value.validateTrainer()
                )
                "amount" -> currentPayment.copy(
                    amount = value,
                    amountError = value.validateAmount()
                )
                "notes" -> currentPayment.copy(
                    notes = value
                )
                else -> currentPayment
            }

            currentState.copy(newTrainerPaymentDetails = updatedPayment)
        }
    }

    fun updateShowConfirmDeleteDialog(show: Boolean, record: TrainerPaymentDto? = null){
        _uiState.update { it.copy(showConfirmDeleteRecordDialog = show, selectedRecord = record) }
    }

    fun updateShowEditPaymentDialog(show: Boolean, record: TrainerPaymentDto? = null){
        _uiState.update { it.copy(
            showEditPaymentDialog = show,
            selectedRecord = record,
            newTrainerPaymentDetails = record?.toTrainerPayment() ?: TrainerPayment()
        ) }
    }

    fun onUpdateRecord(){
        val newPaymentDetails = uiState.value.newTrainerPaymentDetails
        val validatedPayment = newPaymentDetails.copy(
            trainerError = newPaymentDetails.trainer.fullName.validateTrainer(),
            amountError = newPaymentDetails.amount.validateAmount()
        )

        // Update UI state with validated payment
        _uiState.update { it.copy(newTrainerPaymentDetails = validatedPayment) }

        if (!validatedPayment.isValid) {
            when {
                validatedPayment.trainerError != null -> {
                    showToast(validatedPayment.trainerError)
                    return
                }
                validatedPayment.amountError != null -> {
                    showToast(validatedPayment.amountError)
                    return
                }
            }
        } else {
            updateTrainerPayment(newPaymentDetails.toCreateTrainerPaymentDto())

        }
    }

    private fun updateTrainerPayment(request: CreateTrainerPaymentDto) {
        if(_uiState.value.selectedRecord == null){
            showToast("No record to update")
            return
        }
        _uiState.update { it.copy(isLoading = true, loadingMessage = "Updating Record...")  }
        viewModelScope.launch {
            val result = repository.updateTrainerPayment(
                recordID = _uiState.value.selectedRecord!!.id,
                request=request
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
                    fetchTrainerPayments(isRefresh = true)
                }
            }

        }

    }


    fun deleteTrainerPayment(){
        if(_uiState.value.selectedRecord == null){
            showToast("No record to delete")
            return
        }
        _uiState.update { it.copy(isLoading = true, loadingMessage = "Deleting Record...")  }
        viewModelScope.launch {
            val result = repository.deleteTrainerPayment(_uiState.value.selectedRecord!!.id)
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
                    showToast("Record deleted successfully")
                    updateShowConfirmDeleteDialog(false)
                    fetchTrainerPayments(isRefresh = true)
                }
            }

        }

    }


}