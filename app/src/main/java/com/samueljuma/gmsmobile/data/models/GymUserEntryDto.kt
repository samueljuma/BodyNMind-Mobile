package com.samueljuma.gmsmobile.data.models

import kotlinx.serialization.Serializable

@Serializable
data class GymUserEntryDto(
    val username: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val email: String? = null,
    val role: String? = null,
    val phone_number: String? = null,
//    val dob: String? = null,
//    val profile_picture: String? = null,
//    val emergency_contact: String? = null,
)
