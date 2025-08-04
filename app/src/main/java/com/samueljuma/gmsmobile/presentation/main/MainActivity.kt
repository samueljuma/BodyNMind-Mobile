package com.samueljuma.gmsmobile.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.samueljuma.gmsmobile.presentation.navigation.AppNavigation
import com.samueljuma.gmsmobile.presentation.screens.plans.PlansScreen
import com.samueljuma.gmsmobile.ui.theme.GMSMobileTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            val mainViewModel: MainViewModel =  koinViewModel ()

            val snackbarHostState = remember { SnackbarHostState() }
            val isConnected by mainViewModel.isOnline.collectAsStateWithLifecycle()

            GMSMobileTheme {
                LaunchedEffect(isConnected) {
                    if (!isConnected) {
                        snackbarHostState.showSnackbar(
                            message = "No internet connection",
                            duration = SnackbarDuration.Indefinite,
                            withDismissAction = true
                        )
                    } else {
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = MaterialTheme.colorScheme.surface, // Background color
                                contentColor = Color.Black, // Text color
                                dismissActionContentColor = Color.Black
                            )
                        }
                    },
                    content = { innerPadding ->
                        AppNavigation(
                            modifier = Modifier.padding(innerPadding),
                            mainViewModel = mainViewModel
                        )
                    }
                )
            }
        }
    }
}
