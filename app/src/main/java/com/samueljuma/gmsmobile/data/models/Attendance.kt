package com.samueljuma.gmsmobile.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceResponse(
    val status: String,
    val data: List<MemberAttendance>,
    val path: String,
    val method: String,
    val timestamp: String,
    val duration: String
)

@Serializable
data class MemberAttendance(
    val member_name: String,
    val member_id: String,
    val present: Boolean,
    val check_in_time: String?,
    val check_out_time: String?
)

@Serializable
data class MarkAttendanceRequest(
    val members: List<String>,
    val date: String
)
