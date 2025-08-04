package com.samueljuma.gmsmobile.domain

data class DropdownComponent(
    val options: List<TimeFrame>,
    val selectedOption: TimeFrame,
    val onOptionSelected: (TimeFrame) -> Unit
)

enum class TimeFrame(var label: String){
    LAST_7_DAYS("The last 7 days"),
    LAST_30_DAYS("The last 30 days"),
    TODAY("Today")
}
