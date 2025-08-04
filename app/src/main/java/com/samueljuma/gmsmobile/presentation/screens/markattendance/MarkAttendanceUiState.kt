package com.samueljuma.gmsmobile.presentation.screens.markattendance

import com.samueljuma.gmsmobile.domain.models.MemberAttendanceDomain

data class MarkAttendanceUiState(
    val selectedDate: String? = null,
    val attendanceList: List<MemberAttendanceDomain> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val loadingMessage: String? = null
)