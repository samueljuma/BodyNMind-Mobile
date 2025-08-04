package com.samueljuma.gmsmobile.data.network

import android.util.Log
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.nio.channels.UnresolvedAddressException


suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> NetworkResult<T>
): NetworkResult<T> {
    return withContext(dispatcher) {
        try {
            apiCall()
        } catch (e: HttpRequestTimeoutException) {
            NetworkResult.Error("Request Timeout Error")
        } catch (e: ResponseException) {
            val error = when (e.response.status.value) {
                503 -> "Service Temporarily Unavailable"
                500 -> "Internal Server Error"
                404 -> "Resource Not Found"
                else -> "Unexpected error: ${e.response.status}"
            }
            NetworkResult.Error(error)
        } catch (e: UnresolvedAddressException) {
            NetworkResult.Error("Something is wrong, Check internet and try again")
        } catch (e: Exception) {
            Log.d("API ERRORS", "safeApiCall: ${e.message}")
            NetworkResult.Error("An error occurred")
        }
    }
}