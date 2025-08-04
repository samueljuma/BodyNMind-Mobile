package com.samueljuma.gmsmobile.presentation.screens.dashboard

sealed class DashboardEvent {
    object NavigateToMembersList : DashboardEvent()
    object NavigateToTrainersList : DashboardEvent()
    object MarkAttendance: DashboardEvent()
    object NavigateToProfile : DashboardEvent()
    data class ShowError(val message: String) : DashboardEvent()
    data class ShowSuccessMessage(val message: String) : DashboardEvent()
}
