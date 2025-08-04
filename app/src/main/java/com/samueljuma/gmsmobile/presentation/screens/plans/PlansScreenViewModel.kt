package com.samueljuma.gmsmobile.presentation.screens.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samueljuma.gmsmobile.data.models.Plan
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.domain.models.PlanEntry
import com.samueljuma.gmsmobile.domain.models.validateDuration
import com.samueljuma.gmsmobile.domain.models.validatePlanEntry
import com.samueljuma.gmsmobile.domain.models.validatePrice
import com.samueljuma.gmsmobile.domain.repositories.PlansRepository
import com.samueljuma.gmsmobile.utils.toCreateUpdatePlanRequest
import com.samueljuma.gmsmobile.utils.toPlanEntry
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlansScreenViewModel(
    private val plansRepository: PlansRepository
) : ViewModel() {

    private val _plansScreenUiState = MutableStateFlow(PlansScreenUiState())
    val plansScreenUiState = _plansScreenUiState.asStateFlow()

    private val _plansScreenEvent = MutableSharedFlow<PlansScreenEvent>()
    val plansScreenEvent = _plansScreenEvent.asSharedFlow()

    init {
        fetchPlans()
    }

    fun fetchPlans(isRefresh: Boolean = false) {
        _plansScreenUiState.update {
            it.copy(
                loading = !isRefresh,
                isRefreshing = isRefresh,
                loadingMessage = "Fetching plans..."
            )
        }

        viewModelScope.launch {
            val result = plansRepository.fetchSubscriptionPlans()
            when (result) {
                is NetworkResult.Success -> {
                    _plansScreenUiState.update {
                        it.copy(
                            plans = result.data.data.sortedBy { plan -> plan.id },
                            loading = false,
                            loadingMessage = "",
                            isRefreshing = false,
                            error = null
                        )
                    }
                    _plansScreenEvent.emit(PlansScreenEvent.ShowSuccessMessage(
                        if(isRefresh) "Plans refreshed successfully" else "Plans fetched successfully"))
                }

                is NetworkResult.Error -> {
                    _plansScreenUiState.update {
                        it.copy(
                            error = result.message,
                            loading = false,
                            loadingMessage = "",
                            isRefreshing = false,
                        )
                    }
                    _plansScreenEvent.emit(PlansScreenEvent.ShowErrorMessage("Error fetching plans"))

                }

            }
        }
    }

    fun addPlan() {
        viewModelScope.launch {
            val newPlanEntry = _plansScreenUiState.value.newPlanEntry

            //Validate All PLan entry fields
            val validatedEntry = newPlanEntry.validateAllFields()

            _plansScreenUiState.update {
                it.copy(
                    newPlanEntry = validatedEntry
                )
            }

            if(!newPlanEntry.isValid){
                _plansScreenEvent.emit(PlansScreenEvent.ShowErrorMessage("Please fill in all fields correctly"))
                return@launch
            }

            val request = newPlanEntry.toCreateUpdatePlanRequest()

            _plansScreenUiState.update {
                it.copy(
                    loading = true,
                    loadingMessage = "Adding plan..."
                )
            }

            val result = plansRepository.addSubscriptionPlan(request)
            when (result) {
                is NetworkResult.Success -> {
                    _plansScreenUiState.update {
                        it.copy(
                            loading = false,
                            loadingMessage = "",
                            showAddPlanDialog = false,
                            newPlanEntry = PlanEntry()
                        )
                    }
                    fetchPlans(isRefresh = true)
                    _plansScreenEvent.emit(PlansScreenEvent.ShowSuccessMessage("Plan added successfully"))
                }

                is NetworkResult.Error -> {
                    _plansScreenUiState.update {
                        it.copy(
                            loading = false,
                            loadingMessage = ""
                        )
                    }
                    _plansScreenEvent.emit(PlansScreenEvent.ShowErrorMessage(result.message))

                }

            }
        }
    }

    fun updatePlan() {

        viewModelScope.launch {
            val planId = _plansScreenUiState.value.planIdToUpdate ?: return@launch
            val planToUpdate = _plansScreenUiState.value.planEntryToUpdate ?: return@launch

            planToUpdate.validateAllFields()
            if(!planToUpdate.isValid){
                _plansScreenEvent.emit(PlansScreenEvent.ShowErrorMessage("Please fill in all fields correctly"))
                return@launch
            }

            _plansScreenUiState.update {
                it.copy(
                    loading = true,
                    loadingMessage = "Updating plan..."
                )
            }

            val request = planToUpdate.toCreateUpdatePlanRequest()


            val result = plansRepository.updateSubscriptionPlan(planId, request)
            when (result) {
                is NetworkResult.Success -> {
                    _plansScreenUiState.update {
                        it.copy(
                            loading = false,
                            loadingMessage = "",
                            error = null,
                            showEditPlanDialog = false,
                            planEntryToUpdate = null,
                            planIdToUpdate = null
                        )
                    }

                    fetchPlans(isRefresh = true)

                    _plansScreenEvent.emit(PlansScreenEvent.ShowSuccessMessage("Plan updated successfully"))
                }

                is NetworkResult.Error -> {
                    _plansScreenUiState.update {
                        it.copy(
                            loading = false,
                            loadingMessage = "",
                            error = result.message
                        )
                    }
                    _plansScreenEvent.emit(PlansScreenEvent.ShowErrorMessage("Error updating plan"))
                }

            }

        }

    }

    fun deletePlan(planId: Int) {
        _plansScreenUiState.update {
            it.copy(
                loading = true,
                loadingMessage = "Deleting plan..."
            )
        }

        viewModelScope.launch {
            val result = plansRepository.deleteSubscriptionPlan(planId)
            when (result) {
                is NetworkResult.Success -> {
                    _plansScreenUiState.update {
                        it.copy(
                            loading = false,
                            loadingMessage = "",
                            error = null,
                            planIdToDelete = null
                        )
                    }
                    fetchPlans(isRefresh = true)
                    _plansScreenEvent.emit(PlansScreenEvent.ShowSuccessMessage("Plan deleted successfully"))
                }

                is NetworkResult.Error -> {
                    _plansScreenUiState.update {
                        it.copy(
                            loading = false,
                            loadingMessage = "",
                            error = result.message
                        )
                    }
                    _plansScreenEvent.emit(PlansScreenEvent.ShowErrorMessage("Error deleting plan"))
                }

            }
        }
    }

    fun updateNewPlanEntry(value: String, field: String){
        _plansScreenUiState.update { state ->
            val entry = state.newPlanEntry

            val updatedEntry = when(field){
                "name" -> entry.copy(name = value, nameError = value.validatePlanEntry())
                "price" -> entry.copy(price = value, priceError = value.validatePrice())
                "duration" -> entry.copy(duration_days = value, durationError = value.validateDuration())
                "active" -> entry.copy(active = value.toBoolean())
                else -> entry
            }
            state.copy(newPlanEntry = updatedEntry)
        }
    }

    fun updatePlanEntryToUpdate(value: String, field: String){
        _plansScreenUiState.update { state ->
            val entry = state.planEntryToUpdate
            if(entry == null) return@update state

            val updatedEntry = when(field){
                "name" -> entry.copy(name = value, nameError = value.validatePlanEntry())
                "price" -> entry.copy(price = value, priceError = value.validatePrice())
                "duration" -> entry.copy(duration_days = value, durationError = value.validateDuration())
                "active" -> entry.copy(active = value.toBoolean())
                else -> entry
            }
            state.copy(planEntryToUpdate = updatedEntry)
        }
    }


    fun updateShowAddPlanDialog(status: Boolean){
        _plansScreenUiState.update {
            it.copy(
                showAddPlanDialog = status,
                newPlanEntry = PlanEntry()
            )
        }
    }

    fun updateShowEditPlanDialog(status: Boolean){
        _plansScreenUiState.update {
            it.copy(
                showEditPlanDialog = status
            )
        }
    }

    fun setPlanToUpdate(plan: Plan){
        _plansScreenUiState.update {
            it.copy(
                planEntryToUpdate = plan.toPlanEntry(),
                planIdToUpdate = plan.id
            )
        }
    }

    fun setPlanToDelete(planId: Int?){
        _plansScreenUiState.update {
            it.copy(
                planIdToDelete = planId
            )
        }
    }

}