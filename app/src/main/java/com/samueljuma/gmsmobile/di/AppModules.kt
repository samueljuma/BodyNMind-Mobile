package com.samueljuma.gmsmobile.di

import android.util.Log
import com.samueljuma.gmsmobile.data.network.APIService
import com.samueljuma.gmsmobile.domain.repositories.AuthRepository
import com.samueljuma.gmsmobile.domain.repositories.DashboardRepository
import com.samueljuma.gmsmobile.data.session.SessionManager
import com.samueljuma.gmsmobile.domain.repositories.AttendanceRepository
import com.samueljuma.gmsmobile.domain.repositories.ExpensesRepository
import com.samueljuma.gmsmobile.domain.repositories.GymUserRepository
import com.samueljuma.gmsmobile.domain.repositories.PlansRepository
import com.samueljuma.gmsmobile.presentation.main.MainViewModel
import com.samueljuma.gmsmobile.presentation.screens.auth.AuthViewModel
import com.samueljuma.gmsmobile.presentation.screens.dashboard.DashboardViewModel
import com.samueljuma.gmsmobile.presentation.screens.expenses.ExpensesViewModel
import com.samueljuma.gmsmobile.presentation.screens.gymusers.GymUsersViewModel
import com.samueljuma.gmsmobile.presentation.screens.markattendance.MarkAttendanceViewModel
import com.samueljuma.gmsmobile.presentation.screens.plans.PlansScreenViewModel
import com.samueljuma.gmsmobile.presentation.screens.trainerpayments.TrainerPaymentsViewModel
import com.samueljuma.gmsmobile.utils.BASE_URL
import com.samueljuma.gmsmobile.utils.NetworkMonitor
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModules = module {

    single {
        HttpClient(CIO){
            expectSuccess = false

            defaultRequest {
                val sessionManager: SessionManager = get()
                url(BASE_URL)
                val authToken = sessionManager.getAuthToken()

                authToken?.let {
                    headers[HttpHeaders.Authorization] = "Bearer $authToken"
                }
            }

            install(HttpCookies){
                storage = AcceptAllCookiesStorage()
            }

            install(Logging){
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorLogger", message)
                    }
                }
                level = LogLevel.ALL
            }

            install(ContentNegotiation){
                json(
                    Json {
                        encodeDefaults = true
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }

        }
    }

    single { NetworkMonitor(androidContext()) }

    single { SessionManager(androidContext()) }
    single { APIService(get()) }
    single { Dispatchers.IO }
    single { AuthRepository(get(), get(), get()) }
    single { DashboardRepository(get(), get()) }
    single { GymUserRepository(get(), get()) }
    single { AttendanceRepository(get(), get()) }
    single { PlansRepository(get(), get()) }
    single { ExpensesRepository(get(), get()) }


    viewModel { AuthViewModel(get()) }
    viewModel { DashboardViewModel(get()) }
    viewModel { GymUsersViewModel(get()) }
    viewModel { MarkAttendanceViewModel(get()) }
    viewModel { PlansScreenViewModel(get()) }

    viewModel { MainViewModel(get()) }
    viewModel { TrainerPaymentsViewModel(get()) }
    viewModel { ExpensesViewModel(get()) }

}