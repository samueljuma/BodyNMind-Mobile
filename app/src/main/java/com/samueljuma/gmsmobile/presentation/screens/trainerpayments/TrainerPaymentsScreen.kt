package com.samueljuma.gmsmobile.presentation.screens.trainerpayments

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samueljuma.gmsmobile.presentation.screens.common.CustomAppBar
import com.samueljuma.gmsmobile.presentation.screens.common.EmptyUIComponent
import com.samueljuma.gmsmobile.presentation.screens.common.LoadingDialog

@Composable
fun TrainerPaymentsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: TrainerPaymentsViewModel
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchTrainerPayments()
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when(event){
                is TrainerPaymentsEvent.ShowToastMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    when{
        uiState.isLoading -> {
            LoadingDialog(message = uiState.loadingMessage)
        }
    }

    Scaffold(
        topBar = {
            CustomAppBar(
                title = "Trainer Payments",
                navigationIcon = Icons.Default.ArrowBackIosNew,
                actionIcon = {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Filter by Trainer Coming Soon", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                },
                onNavigationIconClick = {
                    navController.popBackStack()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Payment",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        content = {paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                if(!uiState.isLoading){
                    if(uiState.trainerPayments.isEmpty()){
                        EmptyUIComponent(
                            message = "No Trainer Payment Records Found"
                        )
                    }else {
                        TrainerPaymentsTable(
                            trainerPayments = uiState.trainerPayments
                        )
                    }

                }

            }
        }
    )
}