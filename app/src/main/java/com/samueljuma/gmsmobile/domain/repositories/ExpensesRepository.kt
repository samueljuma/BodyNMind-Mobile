package com.samueljuma.gmsmobile.domain.repositories

import com.samueljuma.gmsmobile.data.models.CreateExpenseDto
import com.samueljuma.gmsmobile.data.models.CreateTrainerPaymentDto
import com.samueljuma.gmsmobile.data.models.ErrorResponse
import com.samueljuma.gmsmobile.data.models.ExpenseCategoriesResponse
import com.samueljuma.gmsmobile.data.models.FetchExpensesResponse
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
    suspend fun updateTrainerPayment(recordID: Int, request: CreateTrainerPaymentDto): NetworkResult<Any>{
        return safeApiCall(dispatcher){
            val response = apiService.updateTrainerPayment(recordID, request)
            when(response.status){
                HttpStatusCode.OK -> {
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

    suspend fun fetchExpenses(): NetworkResult<FetchExpensesResponse>{
        return safeApiCall(dispatcher){
            val response = apiService.fetchExpenses()
            when(response.status){
                HttpStatusCode.OK -> {
                    val result = response.body<FetchExpensesResponse>()
                    NetworkResult.Success(result)
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }
        }
    }

    suspend fun createExpense(request: CreateExpenseDto): NetworkResult<Any>{
        return safeApiCall(dispatcher){
            val response = apiService.createExpense(request)
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

    suspend fun deleteExpense(recordID: Int): NetworkResult<Any>{
        return safeApiCall(dispatcher){
            val response = apiService.deleteExpense(recordID)
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

    suspend fun updateExpense(recordID: Int, request: CreateExpenseDto): NetworkResult<Any>{
        return safeApiCall(dispatcher){
            val response = apiService.updateExpense(recordID, request)
            when(response.status){
                HttpStatusCode.OK -> {
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

    suspend fun fetchExpenseCategories(): NetworkResult<ExpenseCategoriesResponse>{
        return safeApiCall(dispatcher){
            val response = apiService.fetchExpenseCategories()
            when(response.status){
                HttpStatusCode.OK -> {
                    val result = response.body<ExpenseCategoriesResponse>()
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