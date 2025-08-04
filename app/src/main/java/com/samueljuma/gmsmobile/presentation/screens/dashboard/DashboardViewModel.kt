package com.samueljuma.gmsmobile.presentation.screens.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samueljuma.gmsmobile.data.models.DashboardRequestParams
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.domain.TimeFrame
import com.samueljuma.gmsmobile.domain.models.GymUserEntry
import com.samueljuma.gmsmobile.domain.repositories.DashboardRepository
import com.samueljuma.gmsmobile.domain.toDashboardSummaryDomain
import com.samueljuma.gmsmobile.domain.toGymUserEntryDto
import com.samueljuma.gmsmobile.presentation.screens.gymusers.GymUserField
import com.samueljuma.gmsmobile.presentation.screens.gymusers.GymUsersScreenEvent
import com.samueljuma.gmsmobile.utils.UserRole
import com.samueljuma.gmsmobile.utils.getDateRange
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository,
) : ViewModel() {

    private val _dashboardUiState = MutableStateFlow(DashboardUiState())
    val dashboardUiState = _dashboardUiState.asStateFlow()

    private val _dashboardEvent = MutableSharedFlow<DashboardEvent>()
    val dashboardEvent = _dashboardEvent.asSharedFlow()

    fun fetchDashboardSummary(
        isRefresh: Boolean = false
    ) {

        val params = _dashboardUiState.value.dashBoardRequestParams

        clearChatData()
        // Update the correct loading state based on the isRefresh flag
        _dashboardUiState.update {
            it.copy(
                isLoading = if (!isRefresh) true else it.isLoading,
                isRefreshing = if (isRefresh) true else it.isRefreshing,
                loadingMessage = "Fetching Dashboard Summary...",
                error = null,
            )
        }

        viewModelScope.launch {
            val result = dashboardRepository.fetchDashboardSummary(params)
            _dashboardUiState.update {
                when (result) {
                    is NetworkResult.Success -> it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        dashboardSummary = result.data.toDashboardSummaryDomain(),
                        error = null,
                        loadingMessage = ""
                    )

                    is NetworkResult.Error -> it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.message,
                        loadingMessage = ""
                    )
                }
            }
        }
    }

    fun updateDashboardRequestParams(timeFrame: TimeFrame) {
        viewModelScope.launch {
            val (startDate, endDate) = getDateRange(timeFrame)
            _dashboardUiState.update {
                it.copy(
                    dashBoardRequestParams = DashboardRequestParams(
                        start_date = startDate,
                        end_date = endDate
                    ),
                    selectedTimeFrame = timeFrame
                )
            }

            //Fetch Dashboard Summary
            fetchDashboardSummary()
        }
    }

    //Clear Chart Data to avoid recomposition when refreshing
    private fun clearChatData(){
        _dashboardUiState.update { state ->
            state.copy(
                dashboardSummary = state.dashboardSummary?.copy(
                    chartData = listOf(0.0),
                    chartLabels = listOf("")
                )
            )
        }
    }

    fun onMarkAttendanceDrawerItemClicked() {
        viewModelScope.launch {
            _dashboardEvent.emit(DashboardEvent.MarkAttendance)
        }
    }

    fun onMembersCardClicked() {
        viewModelScope.launch {
            _dashboardEvent.emit(DashboardEvent.NavigateToMembersList)
        }
    }

    fun onTrainersCardClicked() {
        viewModelScope.launch {
            _dashboardEvent.emit(DashboardEvent.NavigateToTrainersList)
        }
    }

    fun onProfileMenuItemClicked() {
        viewModelScope.launch {
            _dashboardEvent.emit(DashboardEvent.NavigateToProfile)
        }
    }

    fun updateShowAddUserDialog(show: Boolean) {
        _dashboardUiState.update {
            it.copy(
                showAddUserDialog = show
            )
        }
    }

    fun updateGymUserEntry(value: String, field: GymUserField) {
        _dashboardUiState.update { state ->
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

    fun updateGymUserRole(role: String) {
        val gymUserEntry = _dashboardUiState.value.gymUserEntry
        _dashboardUiState.update {
            it.copy(gymUserEntry = gymUserEntry.copy(role = role))
        }
    }

    fun resetGymUserEntry() {
        _dashboardUiState.update {
            it.copy(gymUserEntry = GymUserEntry(), showAddUserDialog = false)
        }
    }

    fun validateAllFields(): Boolean {
        _dashboardUiState.update { state ->
            val validatedEntry = state.gymUserEntry.validateAll()
            state.copy(gymUserEntry = validatedEntry)
        }

        //Log gymUser Entry
        Log.d("GymUserEntry", _dashboardUiState.value.gymUserEntry.toString())

        return _dashboardUiState.value.gymUserEntry.isValid()
    }

    fun addGymUser(gymUserEntry: GymUserEntry) {
        _dashboardUiState.update {
            it.copy(
                isLoading = true,
                loadingMessage = "Adding user..."
            )
        }
        viewModelScope.launch {
            val result = dashboardRepository.addGymUser(gymUserEntry.toGymUserEntryDto())
            when (result) {
                is NetworkResult.Success -> {
                    _dashboardUiState.update {
                        it.copy(
                            isLoading = false,
                            loadingMessage = "",
                            showAddUserDialog = false
                        )
                    }
                    _dashboardEvent.emit(DashboardEvent.ShowSuccessMessage("User added successfully"))
                    // Update the gym user count
                    updateGymUserCount(role = gymUserEntry.role)

                }

                is NetworkResult.Error -> {
                    _dashboardUiState.update {
                        it.copy(
                            isLoading = false,
                            loadingMessage = ""
                        )
                    }
                    _dashboardEvent.emit(DashboardEvent.ShowError(result.message))
                }
            }
        }
    }

    // Update the gym user count based on the role
    private fun updateGymUserCount(role: String) {

        _dashboardUiState.update { currentState ->
            val currentSummary = currentState.dashboardSummary ?: return@update currentState

            val updatedSummary = currentSummary.copy(
                members = if (role.equals(UserRole.MEMBER.string, ignoreCase = true)) {
                    ((currentSummary.members.toIntOrNull() ?: 0) + 1).toString()
                } else currentSummary.members,

                trainers = if (role.equals(UserRole.TRAINER.string, ignoreCase = true)) {
                    ((currentSummary.trainers.toIntOrNull() ?: 0) + 1).toString()
                } else currentSummary.trainers
            )

            Log.d(
                "GymUserCount",
                "Updated counts — Members: ${updatedSummary.members}, Trainers: ${updatedSummary.trainers}"
            )

            currentState.copy(dashboardSummary = updatedSummary)
        }
    }

}

