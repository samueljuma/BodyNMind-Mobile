package com.samueljuma.gmsmobile.data.models

import kotlinx.serialization.Serializable

@Serializable
data class GymUsersResponse(
    val status: String,
    val data: List<User> = emptyList(),
)

@Serializable
data class AddGymUserResponse(
    val status: String,
    val data: User,
)

