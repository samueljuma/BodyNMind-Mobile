package com.samueljuma.gmsmobile.presentation.screens.markattendance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samueljuma.gmsmobile.data.models.MarkAttendanceRequest
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.domain.models.toMemberAttendanceDomain
import com.samueljuma.gmsmobile.domain.repositories.AttendanceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class MarkAttendanceViewModel(
    private val attendanceRepository: AttendanceRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(MarkAttendanceUiState())
    val uiState: StateFlow<MarkAttendanceUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<MarkAttendanceEvent>()
    val event = _event.asSharedFlow()

    init {
        _uiState.update {
            it.copy(
                selectedDate = LocalDate.now().toString(),
            )
        }
        //fetch attendance
//        fetchMembersAttendanceList(LocalDate.now().toString())
        Log.d("MarkAttendanceViewModel", "DATE: ${_uiState.value.selectedDate}")
        fetchMembersAttendanceList(_uiState.value.selectedDate!!)
    }

    fun fetchMembersAttendanceList(date: String){
        _uiState.update {
            it.copy(
                isLoading = true,
                selectedDate = date,
                loadingMessage = "Fetching attendance list...",
                attendanceList = emptyList()
            )
        }

        viewModelScope.launch {
            val result = attendanceRepository.fetchMembersAttendanceList(date)
            when (result){
                is NetworkResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            attendanceList = result.data.data.map {
                                attendance -> attendance.toMemberAttendanceDomain()
                            }.sortedBy { it.present },
                            isLoading = false,
                            loadingMessage = null
                        )
                    }

                    //SUCCESS MESSAGE EVENT HERE
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            error = result.message,
                            isLoading = false,
                            loadingMessage = null
                        )
                    }

                    // ERROR MESSAGE EVENT HERE
                }
            }
        }

    }

    fun markMembersAttendance(){
        viewModelScope.launch {
            val members = uiState.value.attendanceList.filter { it.isChecked }.map { it.member_id }

            if (members.isEmpty()){
                _event.emit(MarkAttendanceEvent.ShowErrorMessage("You have not selected any member"))
                return@launch
            }
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loadingMessage = "Marking attendance..."
                )
            }

            val request = MarkAttendanceRequest(
                members = members,
                date = uiState.value.selectedDate!!
            )
            val result = attendanceRepository.markMembersAttendance(request)
            when(result){
                is NetworkResult.Success -> {
                    _event.emit(MarkAttendanceEvent.ShowSuccessMessage("Attendance marked successfully"))
                    //update present to true for each member
                    _uiState.update {
                        it.copy(
                            attendanceList = it.attendanceList.map { member ->
                                if (members.contains(member.member_id)) {
                                    member.copy(present = true, isChecked = false)
                                } else {
                                    member
                                }
                            },
                            isLoading = false,
                            loadingMessage = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Log.d("MarkAttendanceViewModel", "ERROR: ${result.message}")
                    _uiState.update {
                        it.copy(
                            error = result.message,
                            isLoading = false,
                            loadingMessage = null
                        )
                    }
                    _event.emit(MarkAttendanceEvent.ShowErrorMessage("Error marking attendance"))
                }

            }
        }
    }

    fun updateMemberCheckedState(id: String, isChecked: Boolean) {
        _uiState.update { state ->
            state.copy(
                attendanceList = state.attendanceList.map { member ->
                    if (member.member_id == id) {
                        member.copy(isChecked = isChecked)
                    } else {
                        member
                    }
                }
            )
        }
    }

    fun updateSelectedDate(date: String){
        _uiState.update {
            it.copy(selectedDate = date)
        }
    }
}