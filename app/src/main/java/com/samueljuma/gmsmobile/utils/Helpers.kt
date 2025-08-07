package com.samueljuma.gmsmobile.utils

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.ui.graphics.vector.ImageVector
import com.samueljuma.gmsmobile.R
import com.samueljuma.gmsmobile.data.models.Plan
import com.samueljuma.gmsmobile.domain.TimeFrame
import com.samueljuma.gmsmobile.domain.models.GymUser
import com.samueljuma.gmsmobile.domain.models.Trainer
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun Number.formatedAsCurrency(): String{
    val formatter = DecimalFormat("#,##0")
    return formatter.format(this)
}

fun String.getFirstCharacter():String{
    return this.first().toString().uppercase()
}

fun String.getInitials():String{
    val names = this.split(" ")
    return names.first().getFirstCharacter() + names.last().getFirstCharacter()
}

fun String.extractDateJoined(): String {
    val dateJoined = this.split("T")
    return dateJoined.first()
}

fun Plan.getNameForDisplay(): String{
    return when(name){
        "daily" -> "Daily Plan"
        "monthly" -> "Monthly Plan"
         else -> "Custom Plan"
    }
}

fun String.toInternationalPhone(): String {
    val cleaned = this.filter { it.isDigit() } // Remove all non-digit characters

    return when {
        cleaned.startsWith("254") && cleaned.length == 12 -> cleaned
        cleaned.startsWith("0") && cleaned.length == 10 -> "254" + cleaned.drop(1)
        cleaned.length == 9 && (cleaned.startsWith("7") || cleaned.startsWith("1")) -> "254$cleaned"
        cleaned.length == 10 && (cleaned.startsWith("7") || cleaned.startsWith("1")) -> "254$cleaned"
        cleaned.startsWith("1") && cleaned.length == 9 -> "254$cleaned"
        else -> cleaned
    }
}



//given +254787345794, return 787345794
fun String.stripCountryCode(): String {
    return this.replace("+254", "")
}

fun showDatePickerDialog(
    context: Context,
    today: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val themedContext = ContextThemeWrapper(context, R.style.Theme_GMSMobile)
    val datePickerDialog = DatePickerDialog(
        themedContext,
        { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            onDateSelected(selectedDate)
        },
        today.year,
        today.monthValue - 1, // `-1` because DatePickerDialog months are zero-based
        today.dayOfMonth
    )
    // Set min date (disable past dates)
    datePickerDialog.datePicker.minDate = System.currentTimeMillis()

    datePickerDialog.setOnDismissListener {
        onDismiss()
    }

    // Show the dialog
    datePickerDialog.show()
}

/**
 * Returns a month title string based on the first and last visible dates.
 * If the week spans across two months or years, it shows both.
 *
 * @param firstVisibleDate The first date visible in the current week
 * @param lastVisibleDate The last date visible in the current week
 * @return A formatted string showing the month or months with years if necessary
 */
fun getMonthTitle(firstVisibleDate: LocalDate, lastVisibleDate: LocalDate): String {
    return when {
        // Different years: Show both months with their respective years (e.g., "Dec, 2024 - Jan, 2025")
        firstVisibleDate.year != lastVisibleDate.year -> {
            "${firstVisibleDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, ${firstVisibleDate.year} - " +
                    "${lastVisibleDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, ${lastVisibleDate.year}"
        }
        // Same year, different months: Show both months (e.g., "Sep - Oct, 2024")
        firstVisibleDate.month != lastVisibleDate.month -> {
            "${firstVisibleDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} - " +
                    "${lastVisibleDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, ${firstVisibleDate.year}"
        }
        // Same month and year: Show just one month (e.g., "Sep, 2024")
        else -> {
            "${firstVisibleDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, ${firstVisibleDate.year}"
        }
    }
}


fun getDateRange(timeFrame: TimeFrame): Pair<String, String> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val endDate = LocalDate.now()
    val startDate = when (timeFrame) {
        TimeFrame.TODAY -> endDate
        TimeFrame.LAST_7_DAYS -> endDate.minusDays(6)
        TimeFrame.LAST_30_DAYS -> endDate.minusDays(29)
    }

    return startDate.format(formatter) to endDate.format(formatter)
}

//yyyy-MM-dd
fun LocalDate.formatted(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return this.format(formatter)
}

//Return expiry date i.e if daily return today + duration_days
fun Plan.getPlanExpiryDate(): String{
    return when(this.name){
        "daily" -> LocalDate.now().formatted()
        "monthly" -> LocalDate.now().plusDays(this.duration_days.toLong()).formatted()
        else -> LocalDate.now().plusDays(this.duration_days.toLong()).formatted()
    }
}

fun String.validateAmountToPay(plan: Plan): String? {
    val inputAmount = this.toDoubleOrNull()
    val planPrice = plan.price.toDoubleOrNull()

    if (isBlank()) return "Required"
    if (inputAmount == null) return "Invalid amount"
    if (planPrice == null) return "Invalid plan price"

    return when (plan.name.lowercase()) {
        "daily" -> {
            if (inputAmount < planPrice) "Amount cannot be less than ${plan.price}"
            else null
        }
        "monthly" -> {
            if (inputAmount < planPrice) "Amount must be at least ${plan.price} for a monthly plan"
            else null
        }
        else -> {
            if (inputAmount < planPrice) "Amount should not be less than ${plan.price}"
            else null
        }
    }
}

fun String.getDateFromDateTimeStamp(): String{
    val dateTime = this.split("T")
    return dateTime.first()
}

fun String.formatAmount(): String {
    val formatter = DecimalFormat("#,##0")
    return formatter.format(this.toDoubleOrNull() ?: 0.0)
}

fun String.validateAmount(): String? {
    val inputAmount = this.toDoubleOrNull()
    if (isBlank()) return "Amount is required*"
    if (inputAmount == null) return "Invalid amount"
    return null
}

fun String.validateTrainer(): String? {
    if (this == "Select Trainer") return "Please Select Trainer"
    return null
}

fun String.validateName(): String? {
    if (isBlank()) return "Name is required*"
    return null
}

fun String.validateCategory(): String? {
    if (this == "Select Category") return "Please Select Category"
    return null
}


