package com.samueljuma.gmsmobile.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateTrainerPaymentDto(
    val trainer: Int,
    val amount: String,
    val notes: String,
)
