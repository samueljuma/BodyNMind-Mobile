package com.samueljuma.gmsmobile.presentation.screens.plans

import com.samueljuma.gmsmobile.data.models.Plan
import com.samueljuma.gmsmobile.domain.models.PlanEntry

data class PlansScreenUiState(
    val plans: List<Plan>? = null,
    val loading: Boolean = false,
    val isRefreshing: Boolean = false,
    val loadingMessage: String = "Loading...",
    val error: String? = null,
    val showAddPlanDialog: Boolean = false,
    val showEditPlanDialog: Boolean = false,
    val newPlanEntry: PlanEntry = PlanEntry(),
    val planEntryToUpdate: PlanEntry? = null,
    val planIdToUpdate: Int? = null,
    val planIdToDelete: Int? = null
)