package com.samueljuma.gmsmobile.presentation.screens.plans

sealed class PlansScreenEvent{
    data class ShowSuccessMessage(val message: String): PlansScreenEvent()
    data class ShowErrorMessage(val message: String): PlansScreenEvent()
}