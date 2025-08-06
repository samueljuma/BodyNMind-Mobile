package com.samueljuma.gmsmobile.domain.models

import com.samueljuma.gmsmobile.utils.validateAmount
import com.samueljuma.gmsmobile.utils.validateTrainer

data class TrainerPayment(
    val trainer: Trainer = Trainer(),
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

    fun validate(){
        trainerError ?: trainer.fullName.validateTrainer()
        amountError ?: amount.validateAmount()
    }
}
