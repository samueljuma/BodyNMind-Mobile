package com.samueljuma.gmsmobile.domain.models

import com.samueljuma.gmsmobile.data.models.Plan
import com.samueljuma.gmsmobile.presentation.screens.gymusers.PaymentMethod
import com.samueljuma.gmsmobile.utils.validateAmountToPay

data class PaymentDetails(
    val amountValue: String ="",
    val phoneNumber: String = "",
    val amountError: String? = null,
    val phoneNumberError: String? = null
){
    val noBlankField = amountValue.isNotBlank() && phoneNumber.isNotBlank()
    val isValid = amountError == null && phoneNumberError == null && noBlankField
    fun validate(plan: Plan) {
        this.copy(
            amountError = amountValue.validateAmountToPay(plan),
            phoneNumberError = phoneNumber.validatePhoneNumber()
        )

    }
}