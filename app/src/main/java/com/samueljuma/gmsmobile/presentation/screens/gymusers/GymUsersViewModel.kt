package com.samueljuma.gmsmobile.presentation.screens.gymusers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samueljuma.gmsmobile.data.models.GymUserEntryDto
import com.samueljuma.gmsmobile.data.models.PaymentRequest
import com.samueljuma.gmsmobile.data.models.Plan
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.domain.extractGymUsers
import com.samueljuma.gmsmobile.domain.models.GymUser
import com.samueljuma.gmsmobile.domain.models.GymUserEntry
import com.samueljuma.gmsmobile.domain.models.validatePhoneNumber
import com.samueljuma.gmsmobile.domain.repositories.GymUserRepository
import com.samueljuma.gmsmobile.domain.toGymUserEntryDto
import com.samueljuma.gmsmobile.domain.updateWith
import com.samueljuma.gmsmobile.utils.UserRole
import com.samueljuma.gmsmobile.utils.formatedAsCurrency
import com.samueljuma.gmsmobile.utils.getPlanExpiryDate
import com.samueljuma.gmsmobile.utils.stripCountryCode
import com.samueljuma.gmsmobile.utils.toInternationalPhone
import com.samueljuma.gmsmobile.utils.validateAmountToPay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GymUsersViewModel(
    private val gymUserRepository: GymUserRepository,
): ViewModel() {

    private val _gymUsersScreenUiState = MutableStateFlow(GymUsersScreenUiState())
    val gymUsersScreenUiState = _gymUsersScreenUiState.asStateFlow()

    private val _gymUsersScreenEvent = MutableSharedFlow<GymUsersScreenEvent>()
    val gymUsersScreenEvent = _gymUsersScreenEvent.asSharedFlow()

    fun fetchGymUsers(
        userType: String,
        isRefresh: Boolean = false
    ){

        val loadingMessage = if(userType == UserRole.MEMBER.string) "Fetching members..." else "Fetching trainers..."

        _gymUsersScreenUiState.update {
            it.copy(
                isLoading = if(!isRefresh) true else it.isLoading,
                isRefreshing = if(isRefresh) true else it.isRefreshing,
                loadingMessage = loadingMessage,
                error = null
            )
        }

        viewModelScope.launch {
            val result = gymUserRepository.fetchGymUsers(userType)

            when(result){
                is NetworkResult.Success -> {
                    _gymUsersScreenUiState.update {
                        it.copy(
                            gymUsers = result.data.extractGymUsers().sortedByDescending { it.id },
                            isLoading = false,
                            isRefreshing = false,
                            loadingMessage = "",
                            error = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _gymUsersScreenUiState.update {
                        it.copy(
                            error = result.message,
                            isLoading = false,
                            isRefreshing = false,
                            loadingMessage = "",
                            gymUsers = emptyList()
                        )
                    }
                }
            }
        }

    }

    fun addGymUser(gymUserEntry: GymUserEntry){
        _gymUsersScreenUiState.update {
            it.copy(
                isLoading = true,
                loadingMessage = "Adding user..."
            )
        }
        viewModelScope.launch {
            val result = gymUserRepository.addGymUser(gymUserEntry.toGymUserEntryDto())
            when(result){
                is NetworkResult.Success -> {
                    _gymUsersScreenUiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "User added successfully",
                            loadingMessage = "",
                            showAddUserDialog = false
                        )
                    }
                    _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowSuccessMessage)
                    // Refresh the gym users
                    fetchGymUsers(gymUserEntry.role, isRefresh = true)
                }
                is NetworkResult.Error -> {
                    _gymUsersScreenUiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = null,
                            loadingMessage = ""
                        )
                    }
                    _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowErrorMessage(result.message))
                }
            }
        }
    }

    fun deleteGymUser(gymUser: GymUser){
        _gymUsersScreenUiState.update {
            it.copy(
                isLoading = true,
                loadingMessage = "Deleting user..."
            )
        }
        viewModelScope.launch {
            val result = gymUserRepository.deleteGymUser(gymUser.id)
            val gymUserRole = gymUser.role
            when(result){
                is NetworkResult.Success -> {
                    _gymUsersScreenUiState.update {
                        it.copy(
                            isLoading = false,
                            loadingMessage = "",
                            successMessage = "User deleted successfully",
                            gymUserToDelete = null
                        )
                    }
                    _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowSuccessMessage)
                    //Refresh the gym users
                    fetchGymUsers(gymUserRole!!, isRefresh = true)
                }
                is NetworkResult.Error -> {
                    _gymUsersScreenUiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = null,
                            gymUserToDelete = null,
                            loadingMessage = ""
                        )
                    }
                    _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowErrorMessage(result.message))
                }
            }

        }
    }


    fun updateGymUser(userId: Int, gymUser: GymUserEntryDto){
        _gymUsersScreenUiState.update {
            it.copy(
                isLoading = true,
                loadingMessage = "Updating user..."
            )
        }

        val userRole = if(gymUser.role == UserRole.MEMBER.string) UserRole.MEMBER.string else UserRole.TRAINER.string

        viewModelScope.launch {
            val result = gymUserRepository.updateGymUser(userId,gymUser)
            when(result){
                is NetworkResult.Success -> {
                    _gymUsersScreenUiState.update {
                        it.copy(
                            isLoading = false,
                            loadingMessage = "",
                            successMessage = "User Updated Successfully",
                            gymUserToUpdate = null,
                            gymUserEntry = GymUserEntry() // Reset the gymUserEntry
                        )
                    }
                    _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowSuccessMessage)
                    //Update gym users
//                    updateGymUserInList(userId, gymUser)
                    //Refresh
                    fetchGymUsers(userRole, isRefresh = true)
                }
                is NetworkResult.Error -> {
                    _gymUsersScreenUiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message,
                            loadingMessage = "",
                            gymUserToUpdate = null,
                            successMessage = null,
                            gymUserEntry = GymUserEntry() // Reset the gymUserEntry
                        )
                    }
                    _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowErrorMessage(result.message))
                }
            }
        }
    }

    fun updateGymUserInList(userID: Int, entryDto: GymUserEntryDto) {
        val currentUsers = _gymUsersScreenUiState.value.gymUsers?.toMutableList() ?: mutableListOf()

        val index = currentUsers.indexOfFirst { it.id == userID }

        if (index != -1) {
            val existingUser = currentUsers[index]
            if (entryDto.role != existingUser.role) {
                // Remove user completely
                currentUsers.removeAt(index)
            } else {
                // Replace user with updated version
                currentUsers[index] = existingUser.updateWith(entryDto)
            }

            _gymUsersScreenUiState.update {
                it.copy(gymUsers = currentUsers)
            }
        }
    }

    fun fetchSubscriptionPlans(){
        viewModelScope.launch {
            val result = gymUserRepository.fetchSubscriptionPlans()
            when(result){
                is NetworkResult.Success -> {
                    _gymUsersScreenUiState.update { state->
                        val currentPaymentDetails = state.paymentDetails
                        state.copy(
                            subscriptionPlans = result.data.data.filter { plan -> plan.active },
                            selectedPlan = result.data.data.find { it.name.equals("daily", ignoreCase = true) } ?: result.data.data.firstOrNull(),
                            planExpirationDate = result.data.data.firstOrNull()?.getPlanExpiryDate(),
                            paymentDetails = currentPaymentDetails.copy(
                                amountValue = result.data.data.firstOrNull()?.price ?: ""
                            )
                        )

                    }
                }
                is NetworkResult.Error -> {
                    _gymUsersScreenUiState.update {
                        it.copy(error = result.message)
                    }
                    _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowErrorMessage(result.message))
                }
            }
        }
    }

    fun processMemberPayment(){

        viewModelScope.launch {

            val paymentDetails = _gymUsersScreenUiState.value.paymentDetails
            val selectedPlan = _gymUsersScreenUiState.value.selectedPlan
            if(selectedPlan == null){
                _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowErrorMessage("No plan selected"))
                return@launch
            }
            paymentDetails.validate(selectedPlan)

            if(!paymentDetails.isValid){
                if(_gymUsersScreenUiState.value.paymentRequest?.payment_method == PaymentMethod.MPESA.string){
                    if(paymentDetails.phoneNumberError != null){
                        _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowErrorMessage("Ensure all fields are filled correctly"))
                        return@launch
                    }
                }

            }

            updatePaymentRequest("phone_number", _gymUsersScreenUiState.value.paymentDetails.phoneNumber)

            val currentRequest = _gymUsersScreenUiState.value.paymentRequest
            val paymentMethod = currentRequest?.payment_method ?: ""

            val paymentRequest = currentRequest?.copy(
                phone_number = if(paymentMethod == PaymentMethod.MPESA.string) currentRequest.phone_number.toInternationalPhone() else ""
            ) ?: return@launch

            _gymUsersScreenUiState.update {
                it.copy(
                    paymentRequest = paymentRequest.copy(
                        phone_number = paymentRequest.phone_number.toInternationalPhone(),
                    )
                )
            }

            _gymUsersScreenUiState.update {
                it.copy(
                    isLoading = true,
                    loadingMessage = "Processing payment..."
                )
            }
            val result = gymUserRepository.processMemberPayment(
                paymentRequest = paymentRequest.copy(
                        amount_to_pay = _gymUsersScreenUiState.value.paymentDetails.amountValue,
                        plan_expiry_date = _gymUsersScreenUiState.value.planExpirationDate ?: ""
                )
            )
            when(result){
                is NetworkResult.Success -> {

                    _gymUsersScreenUiState.update {
                        it.copy(
                            isLoading = false,
                            loadingMessage = "",
                            paymentRequest = null,
                            gymUSerToProcessPaymentsFor = null
                        )
                    }
                    if(paymentRequest.payment_method == PaymentMethod.MPESA.string){
                        _gymUsersScreenUiState.update {
                            it.copy(
                                successMessage = "Stk Push Send Successfully",
                            )
                        }
                        //Check if reference is not null and show confirmation dialog
                        val reference = result.extra?.get("reference") ?: return@launch

                        Log.d("PaymentRequest", "payment Reference: $reference")

                        _gymUsersScreenUiState.update {
                            it.copy(paymentReference = reference as String)
                        }
                        // emit confirmation dialog event
                        _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowMpesaConfirmationDialog(
                            reference as String))

                    }else{
                        _gymUsersScreenUiState.update {
                            it.copy(
                                successMessage = "Payment Confirmed",
                            )
                        }
                    }

                    _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowSuccessMessage)
                }
                is NetworkResult.Error -> {
                    _gymUsersScreenUiState.update {
                        it.copy(
                            isLoading = false,
                            loadingMessage = "",
                        )
                    }
                    _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowErrorMessage(result.message))

                }
            }
        }
    }

    fun confirmMpesaPayment(){
        _gymUsersScreenUiState.update {
            it.copy(
                isLoading = true,
                loadingMessage = "Confirming Mpesa Payment..."
            )
        }

        viewModelScope.launch {
            val reference = _gymUsersScreenUiState.value.paymentReference ?: ""
            val result = gymUserRepository.confirmMpesaPayment(reference)
            when(result){
                is NetworkResult.Success -> {
                    _gymUsersScreenUiState.update {
                        it.copy(
                            isLoading = false,
                            loadingMessage = "",
                            paymentReference = "",
                            successMessage = "Payment Confirmed"
                        )
                    }
                    _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowSuccessMessage)

                }
                is NetworkResult.Error -> {
                    _gymUsersScreenUiState.update {
                        it.copy(
                            isLoading = false,
                            loadingMessage = "",
                        )
                    }
                    _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowErrorMessage(result.message))
                }
            }
        }
    }

    private fun initializePaymentRequest(gymUser: GymUser){
        _gymUsersScreenUiState.update {
            it.copy(
                paymentRequest = PaymentRequest(
                    member = gymUser.id,
                    phone_number = gymUser.phone_number?.stripCountryCode() ?: "",
                    plan = it.selectedPlan?.id ?: 0,
                    payment_method = PaymentMethod.MPESA.string,
                    description = "Payment of ${it.selectedPlan?.name} gym plan"
                ),
                paymentDetails = it.paymentDetails.copy(
                    amountValue = it.selectedPlan?.price ?: "",
                    phoneNumber = gymUser.phone_number?.stripCountryCode() ?: ""
                )
            )
        }
        Log.d("PaymentRequest", "PaymentRequest: ${_gymUsersScreenUiState.value.paymentRequest}")
    }

    fun updatePaymentRequest(field: String, value: String){
        _gymUsersScreenUiState.update { state ->
            val paymentRequest = state.paymentRequest
            val updatedPaymentRequest = when(field){
                "phone_number" -> paymentRequest?.copy(phone_number = value)
                "plan" -> paymentRequest?.copy(
                    plan = value.toInt(),
                    description = "Payment of ${state.selectedPlan?.name} gym plan"
                )
                "payment_method" -> paymentRequest?.copy(payment_method = value)
                else -> paymentRequest
            }
            state.copy(paymentRequest = updatedPaymentRequest)
        }

        Log.d("PaymentRequest", "PaymentRequest: ${_gymUsersScreenUiState.value.paymentRequest}")
    }

    fun updateSelectedPlan(plan: Plan){
        _gymUsersScreenUiState.update {
            it.copy(
                selectedPlan = plan,
                planExpirationDate = plan.getPlanExpiryDate(),
                paymentDetails = it.paymentDetails.copy(
                    amountValue = plan.price,
                    amountError = null
                )
            )
        }
        updatePaymentRequest("plan", plan.id.toString())
    }

    fun setGymUserToProcessPaymentsFor(gymUser: GymUser){
        _gymUsersScreenUiState.update {
            it.copy(gymUSerToProcessPaymentsFor = gymUser)
        }

        initializePaymentRequest(gymUser)
    }


    fun onAddUserClicked(userRole: UserRole){
        viewModelScope.launch {
            _gymUsersScreenEvent.emit(GymUsersScreenEvent.ShowAddUserDialog(userRole))
        }
    }

    fun updateShowAddUserDialog(show: Boolean){
        _gymUsersScreenUiState.update {
            it.copy(showAddUserDialog = show)
        }
    }

    fun setGymUserToDelete(gymUser: GymUser?){
        _gymUsersScreenUiState.update {
            it.copy(gymUserToDelete = gymUser)
        }
    }

    fun setGymUserToUpdate(gymUser: GymUser?){
        _gymUsersScreenUiState.update {
            it.copy(gymUserToUpdate = gymUser)
        }
    }

    fun resetStates(){
        _gymUsersScreenUiState.update {
            it.copy(
                error = null,
                successMessage = null,
                isLoading = false,
                loadingMessage = "",
            )
        }
    }

    fun updateGymUserEntry(gymUser: GymUser){
        _gymUsersScreenUiState.update { state ->
            val entry = state.gymUserEntry

            val updatedEntry = entry.copy(
                userName = gymUser.username ?: "",
                firstName = gymUser.first_name ?: "",
                lastName = gymUser.last_name ?: "",
                phoneNumber = gymUser.phone_number?.stripCountryCode() ?: "",
                email = gymUser.email ?: "",
                role = gymUser.role ?: ""
            ).validateAll()

            state.copy(gymUserEntry = updatedEntry)
        }
    }

    fun updateGymUserEntry(value: String, field: GymUserField) {
        _gymUsersScreenUiState.update { state ->
            val entry = state.gymUserEntry

            val updatedEntry = when (field) {
                GymUserField.UserName -> entry.copy(userName = value)
                GymUserField.FirstName -> entry.copy(firstName = value)
                GymUserField.LastName -> entry.copy(lastName = value)
                GymUserField.Email -> entry.copy(email = value)
                GymUserField.PhoneNumber -> entry.copy(phoneNumber = value)
            }.validateEntry(field)

            state.copy(gymUserEntry = updatedEntry)
        }
    }

    fun validateAllFields(): Boolean {
        _gymUsersScreenUiState.update { state ->
            val validatedEntry = state.gymUserEntry.validateAll()
            state.copy(gymUserEntry = validatedEntry)
        }

        //Log gymUser Entry
        Log.d("GymUserEntry", _gymUsersScreenUiState.value.gymUserEntry.toString())

        return _gymUsersScreenUiState.value.gymUserEntry.isValid()
    }

    fun resetGymUserEntry(){
        _gymUsersScreenUiState.update {
            it.copy(gymUserEntry = GymUserEntry(), showAddUserDialog = false)
        }
    }

    fun updateGymUserRole(role: String){
        val gymUserEntry = _gymUsersScreenUiState.value.gymUserEntry
        _gymUsersScreenUiState.update {
            it.copy(gymUserEntry = gymUserEntry.copy(role = role))
        }
    }

    fun updateShowDatePicker(show: Boolean){
        _gymUsersScreenUiState.update {
            it.copy(showDatePicker = show)
        }
    }
    fun updatePlanExpirationDate(date: String){
        _gymUsersScreenUiState.update {
            it.copy(
                planExpirationDate = date,
                showDatePicker = false
            )
        }
    }

    fun adjustPlanPrice(isAdd: Boolean) {
        _gymUsersScreenUiState.update { state ->
            val currentAmount = state.paymentDetails.amountValue.toDoubleOrNull()
            val planPrice = state.selectedPlan?.price?.toDoubleOrNull()

            if (currentAmount == null || planPrice == null) return@update state

            val newAmount = if (isAdd) {
                currentAmount + planPrice
            } else {
                val result = currentAmount - planPrice
                if (result >= planPrice) result else currentAmount
            }

            state.copy(
                paymentDetails = state.paymentDetails.copy(
                    amountValue = newAmount.toInt().toString()
                )
            )
        }
    }

    fun updatePaymentDetails(field: String, value: String){
        _gymUsersScreenUiState.update { state ->
            val selectedPlan = state.selectedPlan

            if(selectedPlan == null) return@update state

            when(field){
                "amount" -> state.copy(
                    paymentDetails = state.paymentDetails.copy(
                        amountValue = value,
                        amountError = value.validateAmountToPay(selectedPlan)
                    )
                )
                "payee_phone" -> state.copy(
                    paymentDetails = state.paymentDetails.copy(
                        phoneNumber = value,
                        phoneNumberError = value.validatePhoneNumber()
                    )
                )
                else -> state
            }
        }
    }

    fun updateShowDialogForConfirmingPayments(show: Boolean){
        _gymUsersScreenUiState.update {
            it.copy(showDialogForConfirmingPayments = show)
        }
    }



}

enum class GymUserField{
    UserName, FirstName, LastName, Email, PhoneNumber
}
