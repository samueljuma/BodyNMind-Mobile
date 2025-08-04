package com.samueljuma.gmsmobile.presentation.screens.dashboard

import com.samueljuma.gmsmobile.data.models.DashboardRequestParams
import com.samueljuma.gmsmobile.domain.TimeFrame
import com.samueljuma.gmsmobile.domain.models.DashboardSummaryDomain
import com.samueljuma.gmsmobile.domain.models.GymUserEntry

data class DashboardUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val dashBoardRequestParams: DashboardRequestParams? = null,
    val dashboardSummary: DashboardSummaryDomain? = null,
    val loadingMessage: String = "Loading...",
    val showAddUserDialog: Boolean = false,
    val gymUserEntry: GymUserEntry = GymUserEntry(),
    val selectedTimeFrame: TimeFrame = TimeFrame.LAST_7_DAYS
)

