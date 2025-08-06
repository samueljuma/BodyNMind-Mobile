package com.samueljuma.gmsmobile.domain

import com.samueljuma.gmsmobile.data.models.CreateTrainerPaymentDto
import com.samueljuma.gmsmobile.data.models.DashboardSummaryResponseDto
import com.samueljuma.gmsmobile.data.models.GymUserEntryDto
import com.samueljuma.gmsmobile.data.models.GymUsersResponse
import com.samueljuma.gmsmobile.domain.models.DashboardSummaryDomain
import com.samueljuma.gmsmobile.domain.models.GymUser
import com.samueljuma.gmsmobile.domain.models.GymUserEntry
import com.samueljuma.gmsmobile.domain.models.TrainerPayment
import com.samueljuma.gmsmobile.utils.extractDateJoined
import com.samueljuma.gmsmobile.utils.formatedAsCurrency

fun DashboardSummaryResponseDto.toDashboardSummaryDomain(): DashboardSummaryDomain {
    val data = this.data

    val activeDailySubs = data.active_subscriptions?.daily ?: 0
    val activeMonthlySubs = data.active_subscriptions?.monthly ?: 0

    var chartLabels = data.attendance_by_date.map { it.date.substring(startIndex = 5) }
    var chartData = data.attendance_by_date.map { it.members_present.toDouble() }

    // Ensure chart has at least two points
    if (chartData.size == 1) {
        chartData = (chartData + 0.0).sorted()
        chartLabels = chartLabels + ""
    }

    return DashboardSummaryDomain(
        members = data.total_members.toString(),
        trainers = data.total_trainers.toString(),
        activeMonthlySubs = activeMonthlySubs.toString(),
        activeDailySubs = activeDailySubs.toString(),
        revenue = data.total_revenue.toInt().formatedAsCurrency(),
        mpesaSales = data.total_mpesa_sales.formatedAsCurrency(),
        cashSales = data.total_cash_sales.toInt().formatedAsCurrency(),
        chartLabels = chartLabels,
        chartData = chartData
    )
}


fun GymUsersResponse.extractGymUsers(): List<GymUser> {

    return data.map {
        GymUser(
            id = it.id,
            username = it.username,
            first_name = it.first_name,
            last_name = it.last_name,
            full_name = "${it.first_name} ${it.last_name}",
            email = it.email,
            role = it.role,
            dob = it.dob,
            date_joined = it.date_joined?.extractDateJoined(),
            profile_picture = it.profile_picture,
            phone_number = it.phone_number,
            emergency_contact = it.emergency_contact,
            is_active = it.is_active,
            self_registered = it.self_registered,
            added_by = it.added_by,
            approved_by = it.approved_by
        )
    }
}

fun List<GymUser>.filterByQuery(query: String): List<GymUser> {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isEmpty()) return this

    return this.filter { user ->
        listOf(user.username, user.full_name, user.email)
            .any { it?.contains(trimmedQuery, ignoreCase = true) == true }
    }
}

fun GymUserEntry.toGymUserEntryDto(): GymUserEntryDto {
    return GymUserEntryDto(
        username = userName,
        first_name = firstName,
        last_name = lastName,
        email = email.ifEmpty { null },
        role = role,
        phone_number = if (phoneNumber.isEmpty()) null else "+254${phoneNumber}"
    )
}

fun GymUser.updateWith(entryDto: GymUserEntryDto): GymUser {
    return this.copy(
        username = entryDto.username ?: this.username,
        first_name = entryDto.first_name ?: this.first_name,
        last_name = entryDto.last_name ?: this.last_name,
        email = entryDto.email ?: this.email,
        role = entryDto.role ?: this.role,
        phone_number = entryDto.phone_number ?: this.phone_number
    )
}

fun TrainerPayment.toCreateTrainerPaymentDto(): CreateTrainerPaymentDto {
    return CreateTrainerPaymentDto(
        trainer = trainer.id,
        amount = amount,
        notes = notes
    )
}

