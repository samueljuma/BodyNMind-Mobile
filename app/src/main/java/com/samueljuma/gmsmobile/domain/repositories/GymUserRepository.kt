package com.samueljuma.gmsmobile.domain.repositories

import com.samueljuma.gmsmobile.data.models.AddGymUserResponse
import com.samueljuma.gmsmobile.data.models.ErrorResponse
import com.samueljuma.gmsmobile.data.models.GymUserEntryDto
import com.samueljuma.gmsmobile.data.models.GymUsersResponse
import com.samueljuma.gmsmobile.data.models.PaymentRequest
import com.samueljuma.gmsmobile.data.models.PaymentResponse
import com.samueljuma.gmsmobile.data.models.StkPushResponse
import com.samueljuma.gmsmobile.data.models.SubscriptionPlansResponse
import com.samueljuma.gmsmobile.data.network.APIService
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.data.network.safeApiCall
import com.samueljuma.gmsmobile.presentation.screens.gymusers.PaymentMethod
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher

class GymUserRepository(
    private val apiService: APIService,
    private val dispatcher: CoroutineDispatcher
) {

    suspend fun fetchGymUsers(userType: String): NetworkResult<GymUsersResponse> {
        return safeApiCall(dispatcher){
            val response = apiService.fetchGymUsers(userType)

            when(response.status){
                HttpStatusCode.OK -> {
                    val result = response.body<GymUsersResponse>()
                    NetworkResult.Success(result)
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }

        }
    }

    suspend fun addGymUser(gymUserEntryDto: GymUserEntryDto): NetworkResult<AddGymUserResponse> {
        return safeApiCall(dispatcher){
            val response = apiService.addGymUser(gymUserEntryDto)

            when(response.status){
                HttpStatusCode.Created -> {
                    val result = response.body<AddGymUserResponse>()
                    NetworkResult.Success(result)
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }
        }

    }

    suspend fun deleteGymUser(userId: Int): NetworkResult<Unit> {
        return safeApiCall(dispatcher){
            val response = apiService.deleteGymUser(userId)
            when(response.status){
                HttpStatusCode.NoContent -> {
                    NetworkResult.Success(Unit)
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }
        }
    }

    suspend fun updateGymUser(userId: Int, gymUser: GymUserEntryDto): NetworkResult<Unit>{
        return safeApiCall(dispatcher){
            val response = apiService.updateGymUser(userId, gymUser)
            when(response.status){
                HttpStatusCode.OK -> { // Check this to be sure
                    NetworkResult.Success(Unit)
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }
        }
    }

    suspend fun fetchSubscriptionPlans(): NetworkResult<SubscriptionPlansResponse>{
        return safeApiCall(dispatcher){
            val response = apiService.fetchSubscriptionPlans()
            when(response.status){
                HttpStatusCode.OK -> {
                    val result = response.body<SubscriptionPlansResponse>()
                    NetworkResult.Success(result)
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }
        }
    }

    suspend fun processMemberPayment(paymentRequest: PaymentRequest): NetworkResult<PaymentResponse>{
        return safeApiCall(dispatcher){
            val response = apiService.processMemberPayment(paymentRequest)
            when(response.status){
                HttpStatusCode.OK -> {
                    when(paymentRequest.payment_method){
                        PaymentMethod.CASH.string -> {
                            val result = response.body<PaymentResponse>()
                            return@safeApiCall NetworkResult.Success(result)
                        }
                        PaymentMethod.MPESA.string -> {
                            val stkPushResponse = response.body<StkPushResponse>()
                            val reference = stkPushResponse.data.reference

                            val confirmationResult = checkIfStkPushHasBeenSent(reference)

                            return@safeApiCall confirmationResult

                        }
                        else -> {
                            val result = response.body<ErrorResponse>()
                            return@safeApiCall NetworkResult.Error(result.message)
                        }
                    }
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }

        }
    }

    private suspend fun checkIfStkPushHasBeenSent(reference: String): NetworkResult<PaymentResponse>{
        return safeApiCall(dispatcher){
            val response = apiService.checkIfPaymentExists(reference)
            when(response.status){
                HttpStatusCode.OK -> {
                    val result = response.body<PaymentResponse>()
                    NetworkResult.Success(result, extra = mapOf("reference" to reference))
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }
        }

    }
    suspend fun confirmMpesaPayment(reference: String): NetworkResult<PaymentResponse>{
        return safeApiCall(dispatcher){
            val response = apiService.confirmMpesaPayment(reference)
            when(response.status){
                HttpStatusCode.OK -> {
                    val result = response.body<PaymentResponse>()
                    NetworkResult.Success(result)
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }
        }

    }

}