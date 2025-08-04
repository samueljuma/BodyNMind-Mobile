package com.samueljuma.gmsmobile.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samueljuma.gmsmobile.data.models.LoginRequest
import com.samueljuma.gmsmobile.data.network.NetworkResult
import com.samueljuma.gmsmobile.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState = _authUiState.asStateFlow()

    private val _navigateToDashboard = MutableSharedFlow<Unit>()
    val navigateToDashboard = _navigateToDashboard.asSharedFlow()

    private val _navigateBackToLoginScreen = MutableSharedFlow<Unit>()
    val navigateBackToLoginScreen = _navigateBackToLoginScreen.asSharedFlow()
    fun login(loginRequest: LoginRequest){
        _authUiState.update { it.copy(
            isLoading = true,
            errorMessage = null
        ) }

        viewModelScope.launch {
            val result = authRepository.login(loginRequest)
            when(result){
                is NetworkResult.Success ->{
                    _authUiState.update { it.copy(
                        isLoading = false,
                        user = result.data,
                        errorMessage = null
                    ) }

                    _navigateToDashboard.emit(Unit)
                }
                is NetworkResult.Error ->{
                    _authUiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.message
                    ) }
                }
            }
        }

    }

    fun logout(){
        viewModelScope.launch {
            authRepository.logout()
            _navigateBackToLoginScreen.emit(Unit)
        }
    }

    fun getUserDetails() = authRepository.getUserDetails()

    fun getAuthCookies() = authRepository.getAuthToken()
}