package com.samueljuma.gmsmobile.utils

import com.samueljuma.gmsmobile.data.models.CreateUpdatePlanRequest
import com.samueljuma.gmsmobile.data.models.Plan
import com.samueljuma.gmsmobile.data.models.TrainerDto
import com.samueljuma.gmsmobile.data.models.TrainerPaymentDto
import com.samueljuma.gmsmobile.data.models.User
import com.samueljuma.gmsmobile.domain.models.PlanEntry
import com.samueljuma.gmsmobile.domain.models.Trainer
import com.samueljuma.gmsmobile.domain.models.TrainerPayment

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

fun User.toTrainer(): Trainer{
    return Trainer(
        id = id,
        username = username ?: "",
        fullName = "$first_name $last_name"
    )
}

fun TrainerPaymentDto.toTrainerPayment(): TrainerPayment {
    return TrainerPayment(
        trainer = trainer.toTrainer(),
        amount = amount,
        notes = notes
    )
}

fun TrainerDto.toTrainer(): Trainer {
    return Trainer(
        id = id,
        username = username,
        fullName = full_name
    )
}