package com.samueljuma.gmsmobile.domain.repositories

import android.util.Log
import com.samueljuma.gmsmobile.data.models.ErrorResponse
import com.samueljuma.gmsmobile.data.models.LoginRequest
import com.samueljuma.gmsmobile.data.models.LoginSuccessResponse
import com.samueljuma.gmsmobile.data.models.User
import com.samueljuma.gmsmobile.data.network.APIService
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.data.session.SessionManager
import com.samueljuma.gmsmobile.domain.models.UserDomain
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.http.HttpStatusCode
import io.ktor.http.parseServerSetCookieHeader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AuthRepository(
    private val apiService: APIService,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val sessionManager: SessionManager
) {

    suspend fun login(loginRequest: LoginRequest): NetworkResult<User>{
        return withContext(coroutineDispatcher){
            try {
                val response = apiService.login(loginRequest)
                when(response.status){
                    HttpStatusCode.OK -> {
                        val loginSuccessResponse = response.body<LoginSuccessResponse>()
                        // Get all Set-Cookie headers
                        val setCookies = response.headers.getAll("Set-Cookie") ?: emptyList()

                        // Parse them using Ktor's utility
                        val cookies = setCookies.map { parseServerSetCookieHeader(it) }

                        val authToken = cookies.find { it.name == "Authentication" }?.value
                        val refreshToken = cookies.find { it.name == "Refresh" }?.value

                        //save authTokens
                        if (authToken != null && refreshToken != null) {
                            sessionManager.saveAuthAndRefreshTokens(authToken, refreshToken)
                        }

                        //Log User
                        Log.d("USERDETAILS", "login: ${loginSuccessResponse.data.user}")
                        //save user details
                        sessionManager.saveUserDetails(loginSuccessResponse.data.user)

                        Log.d("AuthRepository", "Auth: $authToken | Refresh: $refreshToken")
                        NetworkResult.Success(loginSuccessResponse.data.user)
                    }
                    HttpStatusCode.BadRequest -> {
                        NetworkResult.Error("Invalid Credentials")
                    }
                    HttpStatusCode.Unauthorized -> {
                        NetworkResult.Error("UnAuthorized")
                    }
                    else -> {
                        val errorResponse = response.body<ErrorResponse>()
                        Log.d("AuthRepository", "${errorResponse.error}: ${errorResponse.message}")
                        NetworkResult.Error("Oops! Something went wrong")
                    }
                }
            } catch (e: ConnectTimeoutException){
                Log.d("AuthRepository", e.message.toString())
                NetworkResult.Error("Connection Timeout!")
            } catch (e:Exception){
                Log.d("AuthRepository", e.message.toString())
                NetworkResult.Error("Oops! Something went wrong")
            }
        }
    }

    suspend fun logout(){
        withContext(coroutineDispatcher){
            try {
                apiService.logout()
                // Clear all user details including tokens
                sessionManager.clearAllUserDetails()
            }catch (e:Exception){
                Log.d("AuthRepository", e.message.toString())
            }

        }
    }

    fun getUserDetails(): UserDomain{
        return sessionManager.getUserDetails()
    }

    fun getAuthToken() = sessionManager.getAuthToken()
}