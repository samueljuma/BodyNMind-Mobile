package com.samueljuma.gmsmobile.presentation.screens.dashboard

import android.widget.Toast
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samueljuma.gmsmobile.presentation.main.MainViewModel
import com.samueljuma.gmsmobile.presentation.screens.auth.AuthViewModel
import com.samueljuma.gmsmobile.presentation.navigation.AppScreens
import com.samueljuma.gmsmobile.presentation.screens.gymusers.GymUsersViewModel
import com.samueljuma.gmsmobile.utils.UserRole
import kotlinx.coroutines.launch

@Composable
fun DashBoard(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    mainViewModel: MainViewModel,
    navController: NavController
){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val dashboardUiState by dashboardViewModel.dashboardUiState.collectAsStateWithLifecycle()
    val isConnected by mainViewModel.isOnline.collectAsStateWithLifecycle()

    LaunchedEffect(isConnected) {
        if (isConnected) {
            dashboardViewModel.fetchDashboardSummary()
        }
    }

    LaunchedEffect(Unit) {
        authViewModel.navigateBackToLoginScreen.collect{
            navController.navigate(AppScreens.LoginScreen.route){
                popUpTo(AppScreens.DashBoard.route){
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(Unit) {
        dashboardViewModel.dashboardEvent.collect{ event ->
            when(event){
                is DashboardEvent.NavigateToProfile ->{
                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                }
                is DashboardEvent.NavigateToMembersList -> {
                    navController.navigate(AppScreens.GymUsersScreen.createRoute(UserRole.MEMBER.string))
                }
                is DashboardEvent.NavigateToTrainersList -> {
                    navController.navigate(AppScreens.GymUsersScreen.createRoute(UserRole.TRAINER.string))
                }
                is DashboardEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is DashboardEvent.MarkAttendance -> {
                    navController.navigate(AppScreens.MarkAttendance.route)
                }
                is DashboardEvent.ShowSuccessMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DashBoardDrawer(
                userDomain = authViewModel.getUserDetails(),
                onItemSelected = { label ->
                    when(label){
                        DrawerItemLabels.MARK_ATTENDANCE.label -> {
                            // Handle Mark Attendance
                            dashboardViewModel.onMarkAttendanceDrawerItemClicked()
                        }
                        DrawerItemLabels.ADD_USER.label -> {
                            dashboardViewModel.updateShowAddUserDialog(true)
                        }
                        DrawerItemLabels.PLANS.label -> {
                            navController.navigate(AppScreens.PlansScreen.route)
                        }
                        DrawerItemLabels.TRAINER_PAYMENTS.label -> {
                            Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                        }
                        DrawerItemLabels.GYM_EXPENSES.label -> {
                            Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                        }

                        DrawerItemLabels.LOGOUT.label -> {
                            // Handle Logout
                            authViewModel.logout()
                        }
                        else -> {
                            // Handle other cases
                        }
                    }

                    coroutineScope.launch {
                        drawerState.close()
                    }

                }
            )
        }
    ) {
        DashboardContent(
            authViewModel = authViewModel,
            dashboardViewModel = dashboardViewModel,
            onMenuClick = {
                coroutineScope.launch {
                    drawerState.open()
                }
            }
        )
    }

}