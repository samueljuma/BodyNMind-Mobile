package com.samueljuma.gmsmobile.domain.models

data class PlanEntry(
    val name: String = "daily",
    val price: String = "",
    val duration_days: String = "",
    val active: Boolean = true,
    val nameError: String? = null,
    val priceError: String? = null,
    val durationError: String? = null
){
    val noBlankFields = name.isNotBlank() && price.isNotBlank() && duration_days.isNotBlank()

    fun validateAllFields(): PlanEntry{
        return this.copy(
            nameError = name.validatePlanEntry(),
            priceError = price.validatePrice(),
            durationError = duration_days.validateDuration()
        )
    }
    val isValid = nameError == null && priceError == null && durationError == null
            && noBlankFields
}

fun String.validatePlanEntry(): String?{
    when{
        this.isBlank() -> return "Required"
        else -> return null
    }
}

fun String.validatePrice(): String? {
    when {
        this.isBlank() -> return "Required"
        this.toDoubleOrNull() == null -> return "Invalid"
        else -> return null
    }
}
fun String.validateDuration(): String? {
    when {
        this.isBlank() -> return "Required"
        this.toIntOrNull() == null -> return "Invalid"
        else -> return null
    }
}


