package com.samueljuma.gmsmobile.presentation.screens.gymusers

import com.samueljuma.gmsmobile.data.models.PaymentRequest
import com.samueljuma.gmsmobile.data.models.Plan
import com.samueljuma.gmsmobile.domain.models.GymUser
import com.samueljuma.gmsmobile.domain.models.GymUserEntry
import com.samueljuma.gmsmobile.domain.models.PaymentDetails

data class GymUsersScreenUiState(
    val gymUsers: List<GymUser>? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val loadingMessage: String = "",
    val paymentReference: String? = null,
    val successMessage: String? = null,
    val showAddUserDialog: Boolean = false,
    val error: String? = null,
    val gymUserEntry: GymUserEntry = GymUserEntry(),
    val gymUserToDelete: GymUser? = null,
    val gymUserToUpdate: GymUser? = null,
    val gymUSerToProcessPaymentsFor : GymUser? = null,
    val subscriptionPlans: List<Plan>? = null,
    val selectedPlan: Plan? = null,
    val planExpirationDate: String? = null,
    val paymentRequest: PaymentRequest? = null,
    val paymentDetails: PaymentDetails = PaymentDetails(),
    val showDatePicker: Boolean = false,
    val showDialogForConfirmingPayments: Boolean = false
)
