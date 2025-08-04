package com.samueljuma.gmsmobile.domain.models

import com.samueljuma.gmsmobile.data.models.MemberAttendance
import com.samueljuma.gmsmobile.presentation.navigation.AppScreens

data class MemberAttendanceDomain(
    val member_name: String,
    val member_id: String,
    val present: Boolean,
    val check_in_time: String?,
    val check_out_time: String?,
    val isChecked : Boolean,
)

fun MemberAttendance.toMemberAttendanceDomain(): MemberAttendanceDomain {
    return MemberAttendanceDomain(
        member_name = member_name,
        member_id = member_id,
        present = present,
        check_in_time = check_in_time,
        check_out_time = check_out_time,
        isChecked = false
    )
}
