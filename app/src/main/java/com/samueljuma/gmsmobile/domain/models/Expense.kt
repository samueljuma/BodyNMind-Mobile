package com.samueljuma.gmsmobile.domain.models

data class Expense(
    val name: String = "",
    val category: Category = Category(),
    val amount: String = "",
    val notes: String = "",
    val nameError: String? = null,
    val categoryError: String? = null,
    val amountError: String? = null,
    val notesError: String? = null
){
    val noBlankFields: Boolean = name.isNotBlank() && category.id != -1 && amount.isNotBlank()

    val isValid: Boolean = listOf(
        nameError,
        categoryError,
        amountError,
        notesError
    ).all { it == null } && noBlankFields
}

data class Category(
    val id: Int = -1,
    val name: String = "Select Category"
)
