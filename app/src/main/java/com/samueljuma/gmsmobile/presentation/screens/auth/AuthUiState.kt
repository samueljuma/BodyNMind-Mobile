package com.samueljuma.gmsmobile.presentation.screens.auth

import com.samueljuma.gmsmobile.data.models.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null
)