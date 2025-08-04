package com.samueljuma.gmsmobile.domain.models

data class DashboardSummaryDomain(
    val members: String,
    val trainers: String,
    val activeMonthlySubs: String,
    val activeDailySubs: String,
    val revenue: String,
    val mpesaSales: String,
    val cashSales: String,
    val chartLabels: List<String>,
    val chartData: List<Double>
)
