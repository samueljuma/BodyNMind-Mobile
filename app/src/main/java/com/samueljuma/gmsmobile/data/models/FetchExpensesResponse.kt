package com.samueljuma.gmsmobile.data.models

import com.samueljuma.gmsmobile.domain.models.Category
import kotlinx.serialization.Serializable

@Serializable
data class FetchExpensesResponse(
    val status: String,
    val data: List<ExpenseDto>,
    val path: String,
    val method: String,
    val timestamp: String,
    val duration: String
)

@Serializable
data class ExpenseDto(
    val id: Int,
    val name: String,
    val category: CategoryDto,
    val amount: String,
    val notes: String? = null,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class CategoryDto(
    val id: Int,
    val name: String,
    val description: String
)


@Serializable
data class ExpenseCategoriesResponse(
    val status: String,
    val data: List<CategoryDto>,
    val path: String,
    val method: String,
    val timestamp: String,
    val duration: String
)



