package com.samueljuma.gmsmobile.utils

import com.samueljuma.gmsmobile.data.models.Plan
import com.samueljuma.gmsmobile.data.models.TrainerDto
import com.samueljuma.gmsmobile.data.models.TrainerPaymentDto

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

val sampleTrainerPayments = listOf(
    TrainerPaymentDto(
        id = 1,
        trainer = TrainerDto(
            id = 2,
            username = "Coop",
            full_name = "Peter Coop"
        ),
        amount = "2000.00",
        notes = "Payment for July",
        paid_at = "2025-08-06T05:10:58.068892Z",
        updated_at = "2025-08-06T05:10:58.068914Z"
    ),
    TrainerPaymentDto(
        id = 2,
        trainer = TrainerDto(
            id = 3,
            username = "JaneD",
            full_name = "Jane Doe"
        ),
        amount = "1500.00",
        notes = "Payment for July",
        paid_at = "2025-08-05T15:20:30.123456Z",
        updated_at = "2025-08-05T15:20:30.123789Z"
    ),
    TrainerPaymentDto(
        id = 3,
        trainer = TrainerDto(
            id = 4,
            username = "MikeG",
            full_name = "Mike Gordon"
        ),
        amount = "1800.00",
        notes = "Late payment for June",
        paid_at = "2025-08-04T10:00:00.000000Z",
        updated_at = "2025-08-04T10:10:00.000000Z"
    )
)
