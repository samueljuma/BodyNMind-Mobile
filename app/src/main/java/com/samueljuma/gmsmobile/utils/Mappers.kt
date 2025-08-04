package com.samueljuma.gmsmobile.utils

import com.samueljuma.gmsmobile.data.models.CreateUpdatePlanRequest
import com.samueljuma.gmsmobile.data.models.Plan
import com.samueljuma.gmsmobile.domain.models.PlanEntry

fun Plan.toPlanEntry(): PlanEntry {
    return PlanEntry(
        name = name,
        price = price,
        duration_days = duration_days.toString(),
        active = active
    )
}

fun PlanEntry.toCreateUpdatePlanRequest(): CreateUpdatePlanRequest {
    return CreateUpdatePlanRequest(
        name = name,
        price = price,
        duration_days = duration_days.toInt(),
        active = active
    )
}