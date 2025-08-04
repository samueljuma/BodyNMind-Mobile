package com.samueljuma.gmsmobile.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    val member: Int,
    val phone_number: String,
    val plan: Int,
    val payment_method: String,
    val amount_to_pay: String = "",
    val plan_expiry_date: String = "",
    val description: String
)
