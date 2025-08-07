package com.samueljuma.gmsmobile.presentation.screens.trainerpayments

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samueljuma.gmsmobile.presentation.screens.common.CustomAlertDialog
import com.samueljuma.gmsmobile.presentation.screens.common.CustomAppBar
import com.samueljuma.gmsmobile.presentation.screens.common.EmptyUIComponent
import com.samueljuma.gmsmobile.presentation.screens.common.LoadingDialog

@OptIn(ExperimentalMaterial3Api::class)
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
        uiState.showAddPaymentDialog -> {
            TrainerPaymentDetailsDialog(
                onDismiss = { viewModel.updateShowAddPaymentDialog(false) },
                onSaveRecord = { viewModel.onSavePaymentRecord() },
                onFieldChange = { field, value ->
                    viewModel.updateNewTrainerPayment(field, value)
                },
                trainers = uiState.gymTrainers,
                trainerPayment = uiState.newTrainerPaymentDetails
            )
        }
        uiState.showEditPaymentDialog ->{
            TrainerPaymentDetailsDialog(
                onDismiss = { viewModel.updateShowEditPaymentDialog(false) },
                onSaveRecord = { viewModel.onUpdateRecord() },
                onFieldChange = { field, value ->
                    viewModel.updateNewTrainerPayment(field, value)
                },
                trainers = uiState.gymTrainers,
                trainerPayment = uiState.newTrainerPaymentDetails
            )
        }
        uiState.showConfirmDeleteRecordDialog -> {
            CustomAlertDialog(
                dialogTitle = "Delete Record",
                dialogText = "Are you sure you want to delete this record?",
                onDismiss = { viewModel.updateShowConfirmDeleteDialog(false) },
                onConfirm = {
                    viewModel.deleteTrainerPayment()
                },
                icon = Icons.Outlined.Delete,
                iconColor = MaterialTheme.colorScheme.primary
            )
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
                onClick = {viewModel.updateShowAddPaymentDialog(true)}
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
                PullToRefreshBox(
                    modifier = Modifier.weight(1f),
                    isRefreshing = uiState.isRefreshing,
                    contentAlignment = Alignment.TopCenter,
                    onRefresh = { viewModel.fetchTrainerPayments(isRefresh = true) }
                ) {
                    if(!uiState.isLoading){
                        if(uiState.trainerPayments.isEmpty()){
                            EmptyUIComponent(
                                message = "No Trainer Payment Records Found"
                            )
                        }else {
                            TrainerPaymentsTable (
                                trainerPayments = uiState.trainerPayments,
                                onEditRecord = {
                                    viewModel.updateShowEditPaymentDialog(
                                        show = true,
                                        record = it
                                    )
                                },
                                onDeleteRecord = { record ->
                                    viewModel.updateShowConfirmDeleteDialog(
                                        show = true,
                                        record = record
                                    )
                                }
                            )
                        }

                    }
                }


            }
        }
    )
}