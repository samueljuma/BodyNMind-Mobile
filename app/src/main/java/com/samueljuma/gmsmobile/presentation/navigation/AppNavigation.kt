package com.samueljuma.gmsmobile.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samueljuma.gmsmobile.presentation.main.MainViewModel
import com.samueljuma.gmsmobile.presentation.screens.auth.AuthViewModel
import com.samueljuma.gmsmobile.presentation.screens.auth.LoginScreen
import com.samueljuma.gmsmobile.presentation.screens.dashboard.DashBoard
import com.samueljuma.gmsmobile.presentation.screens.gymusers.GymUsersScreen
import com.samueljuma.gmsmobile.presentation.screens.markattendance.MarkAttendanceScreen
import com.samueljuma.gmsmobile.presentation.screens.plans.PlansScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(
    modifier: Modifier,
    mainViewModel: MainViewModel
) {

    val navController = rememberNavController()
    val authViewModel = koinViewModel<AuthViewModel>()

    val isLoggedIn = authViewModel.getAuthCookies() != null

    val startDestination = if (isLoggedIn) AppScreens.DashBoard.route else AppScreens.LoginScreen.route


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppScreens.LoginScreen.route) {
            LoginScreen(
                navController = navController,
                authViewModel = koinViewModel()
            )
        }
        composable(AppScreens.DashBoard.route) {
            DashBoard(
                authViewModel = authViewModel,
                navController = navController,
                dashboardViewModel = koinViewModel(),
                mainViewModel = mainViewModel
            )
        }
        composable(
            route = AppScreens.GymUsersScreen.route,
            arguments = listOf(navArgument("userType") { type = NavType.StringType} )
        ) { backStackEntry ->
            GymUsersScreen(
                userType = backStackEntry.arguments?.getString("userType") ?: "unknown",
                onNavigationIconClick = { navController.navigateUp() },
                gymUsersViewModel = koinViewModel(),
                navController = navController
            )
        }
        composable(AppScreens.MarkAttendance.route){
            MarkAttendanceScreen(
                navController = navController,
                markAttendanceViewModel = koinViewModel()
            )

        }
        composable(AppScreens.PlansScreen.route)  {
            PlansScreen(
                navController = navController,
                plansScreenViewModel = koinViewModel(),
            )
        }

    }
}