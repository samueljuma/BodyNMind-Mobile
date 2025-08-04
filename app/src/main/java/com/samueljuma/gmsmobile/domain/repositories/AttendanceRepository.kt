package com.samueljuma.gmsmobile.domain.repositories

import com.samueljuma.gmsmobile.data.models.AttendanceResponse
import com.samueljuma.gmsmobile.data.models.ErrorResponse
import com.samueljuma.gmsmobile.data.models.MarkAttendanceRequest
import com.samueljuma.gmsmobile.data.network.APIService
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.data.network.safeApiCall
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher

class AttendanceRepository(
    private val apiService: APIService,
    private val dispatcher: CoroutineDispatcher
) {

    suspend fun fetchMembersAttendanceList(date: String): NetworkResult<AttendanceResponse> {
        return  safeApiCall(dispatcher){
            val response = apiService.fetchMembersAttendanceList(date)
            when(response.status){
                HttpStatusCode.OK -> {
                    val result = response.body<AttendanceResponse>()
                    NetworkResult.Success(result)
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }
        }
    }

    suspend fun markMembersAttendance(request: MarkAttendanceRequest): NetworkResult<Any>{
        return safeApiCall(dispatcher){
            val response = apiService.markMembersAttendance(request)
            when(response.status){
                HttpStatusCode.Created -> {
                    NetworkResult.Success(response.body())
                }
                else -> {
                    val result = response.body<ErrorResponse>()
                    NetworkResult.Error(result.message)
                }
            }
        }
    }
}