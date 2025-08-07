package com.samueljuma.gmsmobile.presentation.screens.expenses

import android.widget.Toast
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
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
fun ExpensesScreen(
    modifier: Modifier,
    viewModel: ExpensesViewModel,
    navController: NavController
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is ExpensesEvent.ShowToastMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    when{
        uiState.isLoading -> {
            LoadingDialog(message = uiState.loadingMessage)
        }
        uiState.showAddExpenseDialog -> {
            ExpensesDialog(
                expense = uiState.newExpenseDetails,
                categories = uiState.expenseCategories,
                onDismiss = { viewModel.updateShowAddExpenseDialog(false) },
                onSaveRecord = { viewModel.onSaveExpenseRecord() },
                onFieldChange = { field, value ->
                    viewModel.updateNewExpenseDetails(field, value)
                }
            )
        }
        uiState.showEditPaymentDialog -> {
            ExpensesDialog(
                expense = uiState.newExpenseDetails,
                categories = uiState.expenseCategories,
                onDismiss = { viewModel.updateShowEditPaymentDialog(false)},
                onSaveRecord = { viewModel.onUpdateRecord() },
                onFieldChange = { field, value ->
                    viewModel.updateNewExpenseDetails(field, value)
                }
            )
        }
        uiState.showConfirmDeleteRecordDialog -> {
            CustomAlertDialog(
                dialogTitle = "Delete Record",
                dialogText = "Are you sure you want to delete this record?",
                onDismiss = { viewModel.updateShowConfirmDeleteDialog(false) },
                onConfirm = { viewModel.deleteExpense() },
                icon = Icons.Outlined.Delete,
                iconColor = MaterialTheme.colorScheme.error
            )
        }
    }


    Scaffold(
        topBar ={
            CustomAppBar(
                title = "Expenses",
                navigationIcon = Icons.Default.ArrowBackIosNew,
                onNavigationIconClick = { navController.popBackStack() },
                actionIcon = {
                    IconButton(
                        onClick = {
                            viewModel.showToast("Filter by category Coming Soon")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.updateShowAddExpenseDialog(true) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Payment",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ) {
                PullToRefreshBox(
                    modifier = Modifier.weight(1f),
                    isRefreshing = uiState.isRefreshing,
                    contentAlignment = Alignment.TopCenter,
                    onRefresh = { viewModel.fetchExpenses(isRefresh = true) }
                ){
                    if(!uiState.isLoading){
                        if(uiState.expenses.isEmpty()){
                            EmptyUIComponent(
                                message = "No Expense Records Found"
                            )
                        }else {
                            ExpensesTable(
                                expenses = uiState.expenses,
                                onEditClick = {
                                    viewModel.updateShowEditPaymentDialog(
                                        show = true,
                                        record = it
                                    )
                                },
                                onDeleteClick = {
                                    viewModel.updateShowConfirmDeleteDialog(
                                        show = true,
                                        record = it
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

