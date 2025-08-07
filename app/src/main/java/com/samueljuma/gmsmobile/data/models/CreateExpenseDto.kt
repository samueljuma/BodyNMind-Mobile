package com.samueljuma.gmsmobile.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateExpenseDto(
    val name: String,
    val category_id: String,
    val amount: String,
)
