package com.samueljuma.gmsmobile.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionPlansResponse(
    val status: String,
    val data: List<Plan>,
    val path: String,
    val method: String,
    val timestamp: String,
    val duration: String
)

@Serializable
data class Plan(
    val id: Int,
    val name: String,
    val price: String,
    val duration_days: Int,
    val active: Boolean
)

@Serializable
data class CreateUpdatePlanRequest(
    val name: String,
    val price: String,
    val duration_days: Int,
    val active: Boolean = true
)
