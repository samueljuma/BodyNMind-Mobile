package com.samueljuma.gmsmobile.domain.models

data class UserDomain(
    val id: Int,
    val username: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val email: String? = null,
    val role: String? = null,
    val dob: String? = null,
    val profile_picture: String? = null,
    val phone_number: String? = null,
    val emergency_contact: String? = null
)
