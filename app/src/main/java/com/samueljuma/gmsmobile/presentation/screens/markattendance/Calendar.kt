package com.samueljuma.gmsmobile.presentation.screens.markattendance


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.samueljuma.gmsmobile.utils.getMonthTitle
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun Calendar(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
    val today = LocalDate.now()
    val currentWeekStart = remember { today.with(DayOfWeek.MONDAY) } // Start of the current week
    val currentMonth = remember { YearMonth.now() }
    val startDate = remember { currentMonth.minusMonths(100).atStartOfMonth() }
    val endDate = remember { currentMonth.plusMonths(100).atEndOfMonth() }

    // Track the currently visible month
    var visibleMonth by remember { mutableStateOf(YearMonth.from(today)) }

    val state = rememberWeekCalendarState(
        startDate = startDate,
        endDate = endDate,
        firstVisibleWeekDate = today,
        firstDayOfWeek = daysOfWeek.first()
    )

    val coroutineScope = rememberCoroutineScope()

    // Observe when the visible week changes and update the month accordingly
    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleWeek }
            .collect { week ->
                val firstVisibleDate = week.days.first().date
                // Update the visible month if the visible week crosses into a new month
                val newMonth = YearMonth.from(firstVisibleDate)
                if (newMonth != visibleMonth) {
                    visibleMonth = newMonth
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomEnd = 18.dp,
                    bottomStart = 18.dp
                )
            )
            .background(MaterialTheme.colorScheme.primary)
    ) {
        val firstVisibleDate = state.firstVisibleWeek.days.first().date
        val lastVisibleDate = state.firstVisibleWeek.days.last().date
        val monthTitle = getMonthTitle(firstVisibleDate, lastVisibleDate)

        // MonthHeader displays the currently visible month and handles navigation
        MonthHeader(
            monthTitle = monthTitle,
            onPreviousClick = {
                coroutineScope.launch {
                    val previousWeek = state.firstVisibleWeek.days.first().date.minusWeeks(1)

                    state.scrollToWeek(previousWeek)

                    // Update the visible month if we move to a previous month
                    if (YearMonth.from(previousWeek) != visibleMonth) {
                        visibleMonth = YearMonth.from(previousWeek)
                    }

                }
            },

            onNextClick = {
                coroutineScope.launch {
                    val nextWeek = state.firstVisibleWeek.days.first().date.plusWeeks(1)

                    // Only scroll to previous week if we're beyond the current week
                    val canGoToNextWeek = nextWeek <= currentWeekStart
                    if (canGoToNextWeek) {
                        state.scrollToWeek(nextWeek)

                        // Update the visible month if we move to a next month
                        if (YearMonth.from(nextWeek) != visibleMonth) {
                            visibleMonth = YearMonth.from(nextWeek)
                        }
                    }
                }

            },
            isNextEnabled = state.firstVisibleWeek.days.first().date < currentWeekStart // Disable button when at current week
        )

        // Days of the week titles (Mon, Tue, etc.)
        DaysOfWeekTitle(daysOfWeek, state.firstVisibleWeek.days.first().date)

        // WeekCalendar for displaying days in a week
        WeekCalendar(
            state = state,
            dayContent = { day ->
                Day(
                    day = day,
                    isSelected = day.date == selectedDate,
                    onDateSelected = { date ->
                        onDateSelected(date)
                    }
                )
            },
            userScrollEnabled = false
        )
        Spacer(modifier = Modifier.height(10.dp))
    }


}

@Composable
fun Day(
    day: WeekDay,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    // Get the context
    val context = LocalContext.current

    val isToday = day.date == LocalDate.now()
    val isPastDate = day.date.isBefore(LocalDate.now())
    val isClickable = isPastDate || isToday

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .clickable(enabled = isClickable) {
                if (isClickable) {
                    onDateSelected(day.date)
                }

            }
            .background(
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
//                    isToday -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> Color.Transparent
                },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = when {
                isSelected -> MaterialTheme.colorScheme.primary
                isClickable -> Color.White
                else -> Color.White.copy(alpha = 0.5f)
            },
            fontWeight = when {
                isClickable -> FontWeight.Bold
                else -> FontWeight.Normal
            }
        )
    }
}


@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>, currentWeekStart: LocalDate) {
    val today = LocalDate.now()

    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            // Get the corresponding date for each day of the week in the current week
            val currentWeekDayDate = currentWeekStart.with(dayOfWeek)

            // Determine if the current week day is before today
            val isPastWeekDayOrToday = currentWeekDayDate.isBefore(today) || currentWeekDayDate == today

            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                color = if (isPastWeekDayOrToday) Color.White else Color.White.copy(alpha = 0.4f)
            )
        }
    }
}


@Composable
fun MonthHeader(
    monthTitle: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    isNextEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp, horizontal = 40.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous week button
        IconButton(
            onClick = onPreviousClick,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "Previous Week",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Month and year display
        Text(
            text = monthTitle,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        // Next week button
        IconButton(
            onClick = onNextClick,
            enabled = isNextEnabled
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Next Week",
                modifier = Modifier.size(18.dp),
                tint = if (isNextEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(
                    alpha = 0.4f
                )
            )
        }
    }
}
