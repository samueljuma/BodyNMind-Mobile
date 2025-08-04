package com.samueljuma.gmsmobile.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val email: String? = null,
    val role: String? = null,
    val dob: String? = null,
    val date_joined: String? = null,
    val profile_picture: String? = null,
    val phone_number: String? = null,
    val emergency_contact: String? = null,
    val is_active: Boolean? = null,
    val self_registered: Boolean? = null,
    val added_by: Approver? = null,
    val approved_by: Approver? = null
)

@Serializable
data class Approver(
    val id: Int? = null,
    val username: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val role: String? = null,
)

@Serializable
data class LoginSuccessResponse(
    val status: String,
    val data: LoginData,
)

@Serializable
data class LoginData(
    val user: User
)

@Serializable
data class ErrorResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val error: String,
)

