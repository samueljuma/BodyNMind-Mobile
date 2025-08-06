package com.samueljuma.gmsmobile.data.models

import kotlinx.serialization.Serializable


@Serializable
data class FetchTrainerPaymentsResponse(
    val status: String,
    val data: List<TrainerPaymentDto>,
    val path: String,
    val method: String,
    val timestamp: String,
    val duration: String
)

@Serializable
data class TrainerPaymentDto(
    val id: Int,
    val trainer: TrainerDto,
    val amount: String,
    val notes: String,
    val paid_at: String,
    val updated_at: String
)

@Serializable
data class TrainerDto(
    val id: Int,
    val username: String,
    val full_name: String
)

