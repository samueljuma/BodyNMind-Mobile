package com.samueljuma.gmsmobile.data.models

import kotlinx.serialization.Serializable

@Serializable
data class DashboardSummaryResponseDto(
    val status: String,
    val `data`: SummaryData,
)

@Serializable
data class SummaryData(
    val total_members: Int,
    val total_trainers: Int,
    val total_revenue: Double,
    val total_mpesa_sales: Double,
    val total_cash_sales: Double,
    val attendance_by_date: List<AttendanceByDate>,
    val active_subscriptions: ActiveSubscriptions? = null
)

@Serializable
data class AttendanceByDate(
    val date: String,
    val members_present: Int,
    val revenue: Double,
    val mpesa_sales: Double,
    val cash_sales: Double
)

@Serializable
data class ActiveSubscriptions(
    val custom: Int? = null,
    val daily: Int? = null,
    val monthly: Int? = null
)

data class DashboardRequestParams(
    val start_date: String,
    val end_date: String
)
