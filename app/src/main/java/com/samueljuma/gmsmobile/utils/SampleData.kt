package com.samueljuma.gmsmobile.utils

import com.samueljuma.gmsmobile.data.models.Plan

val samplePlan = Plan(
    id = 1,
    name = "Daily Plan",
    price = "100",
    duration_days = 1,
    active = true
)
val samplePlans = listOf(
    Plan(
        id = 1,
        name = "Daily Plan",
        price = "100",
        duration_days = 1,
        active = true
    ),
    Plan(
        id = 2,
        name = "Monthly Plan",
        price = "2000",
        duration_days = 30,
        active = true
    ),
    Plan(
        id = 3,
        name = "Custom Plan",
        price = "0",
        duration_days = 0,
        active = false
    )

)