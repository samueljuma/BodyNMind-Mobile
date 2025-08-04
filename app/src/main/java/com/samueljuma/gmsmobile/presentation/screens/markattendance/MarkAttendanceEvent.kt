package com.samueljuma.gmsmobile.presentation.screens.markattendance

sealed class MarkAttendanceEvent {
    data class ShowErrorMessage(val message: String): MarkAttendanceEvent()
    data class ShowSuccessMessage(val message: String): MarkAttendanceEvent()

}