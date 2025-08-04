package com.samueljuma.gmsmobile.domain.repositories

import com.samueljuma.gmsmobile.data.models.AddGymUserResponse
import com.samueljuma.gmsmobile.data.models.DashboardRequestParams
import com.samueljuma.gmsmobile.data.models.DashboardSummaryResponseDto
import com.samueljuma.gmsmobile.data.models.ErrorResponse
import com.samueljuma.gmsmobile.data.models.GymUserEntryDto
import com.samueljuma.gmsmobile.data.network.APIService
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.data.network.safeApiCall
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DashboardRepository(
    private val apiService: APIService,
    private val dispatcher: CoroutineDispatcher
){

    suspend fun fetchDashboardSummary(
        params: DashboardRequestParams? = null
    ): NetworkResult<DashboardSummaryResponseDto> {

        return safeApiCall(dispatcher){
            val response = apiService.fetchDashboardSummary(params)
            when(response.status){
                HttpStatusCode.OK -> {
                    val result = response.body<DashboardSummaryResponseDto>()
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

}