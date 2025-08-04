package com.samueljuma.gmsmobile.presentation.main

import android.app.Application
import com.samueljuma.gmsmobile.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GMSMobileApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModules)
            androidContext(this@GMSMobileApplication)

        }
    }
}