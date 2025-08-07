package com.samueljuma.gmsmobile.presentation.screens.expenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samueljuma.gmsmobile.presentation.screens.common.CustomAppBar
import com.samueljuma.gmsmobile.presentation.screens.common.LoadingDialog

@Composable
fun ExpensesScreen(
    modifier: Modifier,
    viewModel: ExpensesViewModel,
    navController: NavController
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when{
        uiState.isLoading -> {
            LoadingDialog(message = uiState.loadingMessage)
        }
    }


    Scaffold(
        topBar ={
            CustomAppBar(
                title = "Expenses",
                navigationIcon = Icons.Default.ArrowBackIosNew,
                onNavigationIconClick = { navController.popBackStack() }
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
                Text(
                    text = "${uiState.expenses.size} Expenses found",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    )
}

