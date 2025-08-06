package com.samueljuma.gmsmobile.presentation.navigation

sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")
    object DashBoard : AppScreens("dashboard_screen")
    object GymUsersScreen : AppScreens("gym_users_screen/{userType}"){
        fun createRoute(userType: String) = "gym_users_screen/$userType"
    }
    object MarkAttendance : AppScreens("mark_attendance_screen")

    object PlansScreen : AppScreens("plans_screen")
    object TrainerPaymentsScreen : AppScreens("trainer_payments_screen")


}