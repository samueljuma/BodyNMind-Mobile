package com.samueljuma.gmsmobile.domain.repositories

import com.samueljuma.gmsmobile.data.models.CreateUpdatePlanRequest
import com.samueljuma.gmsmobile.data.models.ErrorResponse
import com.samueljuma.gmsmobile.data.models.SubscriptionPlansResponse
import com.samueljuma.gmsmobile.data.network.APIService
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.data.network.safeApiCall
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher

class PlansRepository(
    private val apiService: APIService,
    private val dispatcher: CoroutineDispatcher
) {

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

    suspend fun addSubscriptionPlan(request: CreateUpdatePlanRequest): NetworkResult<Any>{
        return safeApiCall(dispatcher){
            val response = apiService.addSubscriptionPlan(request)
            when(response.status){
                HttpStatusCode.Created -> {
                    NetworkResult.Success(response.body<Any>())
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }
        }
    }

    suspend fun updateSubscriptionPlan(planId: Int, request: CreateUpdatePlanRequest): NetworkResult<Any>{
        return safeApiCall(dispatcher){
            val response = apiService.updateSubscriptionPlan(planId,request)
            when(response.status){
                HttpStatusCode.OK -> {
                    NetworkResult.Success(response.body<Any>())
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }
        }
    }

    suspend fun deleteSubscriptionPlan(planId: Int): NetworkResult<Any>{
        return safeApiCall(dispatcher){
            val response = apiService.deleteSubscriptionPlan(planId)
            when(response.status){
                HttpStatusCode.NoContent -> {
                    NetworkResult.Success(response.body<Any>())
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }
        }
    }
}