package com.samueljuma.gmsmobile.domain.models

data class TrainerPayment(
    val trainer: Trainer = Trainer(-1, "", "Select Trainer"),
    val amount: String = "",
    val notes: String = "",
    val trainerError: String? = null,
    val amountError: String? = null,
    val notesError: String? = null
){
    val noBlankFields: Boolean = trainer.id != -1 && amount.isNotBlank()

    val isValid: Boolean = listOf(
        trainerError,
        amountError,
        notesError
    ).all { it == null } && noBlankFields
}
