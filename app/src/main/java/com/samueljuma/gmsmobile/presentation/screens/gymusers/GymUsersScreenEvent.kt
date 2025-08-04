package com.samueljuma.gmsmobile.presentation.screens.gymusers

import com.samueljuma.gmsmobile.domain.models.GymUser
import com.samueljuma.gmsmobile.utils.UserRole

sealed class GymUsersScreenEvent{
    data class ShowAddUserDialog(val userRole: UserRole): GymUsersScreenEvent()
    data class ShowErrorMessage(val message: String): GymUsersScreenEvent()
    object ShowSuccessMessage: GymUsersScreenEvent()
    data class ShowMpesaConfirmationDialog(val reference: String): GymUsersScreenEvent()
}
