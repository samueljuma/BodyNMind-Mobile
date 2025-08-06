package com.samueljuma.gmsmobile.domain.models

import com.samueljuma.gmsmobile.data.models.Approver

data class GymUser(
    val id: Int,
    val username: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val full_name: String? = null,
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

data class Trainer(
    val id: Int,
    val username: String,
    val fullName: String
)
