package com.samueljuma.gmsmobile.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentResponse(
    val status: String,
    val data: PaymentData
)

@Serializable
data class PaymentData(
    val id: Int,
    val amount: String,
    val payment_method: String,
    val reference: String,
    val updated_at: String,
    val status: String,
    val status_details: String? = null,
    val created_at: String,
    val member: Int,
    val plan: Int,
)

@Serializable
data class StkPushResponse(
    val status: String,
    val data: StkPushData,
)

@Serializable
data class StkPushData(
    val reference: String,
    val mpesa_response: MpesaResponse
)

@Serializable
data class MpesaResponse(
    val MerchantRequestID: String,
    val CheckoutRequestID: String,
    val ResponseCode: String,
    val ResponseDescription: String,
    val CustomerMessage: String
)

