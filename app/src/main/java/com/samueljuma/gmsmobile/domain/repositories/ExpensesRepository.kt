package com.samueljuma.gmsmobile.domain.repositories

import com.samueljuma.gmsmobile.data.models.CreateTrainerPaymentDto
import com.samueljuma.gmsmobile.data.models.ErrorResponse
import com.samueljuma.gmsmobile.data.models.FetchTrainerPaymentsResponse
import com.samueljuma.gmsmobile.data.models.GymUsersResponse
import com.samueljuma.gmsmobile.data.network.APIService
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.data.network.safeApiCall
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher

class ExpensesRepository(
    private val apiService: APIService,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun fetchTrainerPayments(): NetworkResult<FetchTrainerPaymentsResponse>{
        return safeApiCall(dispatcher){
            val response = apiService.fetchTrainerPayments()
            when(response.status){
                HttpStatusCode.OK -> {
                    val result = response.body<FetchTrainerPaymentsResponse>()
                    NetworkResult.Success(result)
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }
        }

    }

    suspend fun fetchTrainers(userType: String): NetworkResult<GymUsersResponse> {
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

    suspend fun createTrainerPayment(request: CreateTrainerPaymentDto): NetworkResult<Any>{
        return safeApiCall(dispatcher){
            val response = apiService.createTrainerPayment(request)
            when(response.status){
                HttpStatusCode.Created -> {
                    val result = response.body<Any>()
                    NetworkResult.Success(result)
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }

        }
    }

    suspend fun deleteTrainerPayment(recordID: Int): NetworkResult<Any>{
        return safeApiCall(dispatcher){
            val response = apiService.deleteTrainerPayment(recordID)
            when(response.status){
                HttpStatusCode.NoContent -> {
                    val result = response.body<Any>()
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