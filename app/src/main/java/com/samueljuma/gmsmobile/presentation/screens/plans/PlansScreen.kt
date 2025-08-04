package com.samueljuma.gmsmobile.presentation.screens.plans

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesomeMotion
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samueljuma.gmsmobile.data.models.Plan
import com.samueljuma.gmsmobile.presentation.screens.common.CustomAlertDialog
import com.samueljuma.gmsmobile.presentation.screens.common.CustomAppBar
import com.samueljuma.gmsmobile.presentation.screens.common.EmptyUIComponent
import com.samueljuma.gmsmobile.presentation.screens.common.ErrorUIComponent
import com.samueljuma.gmsmobile.presentation.screens.common.LoadingDialog
import com.samueljuma.gmsmobile.utils.getNameForDisplay
import com.samueljuma.gmsmobile.utils.samplePlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(
    navController: NavController,
    plansScreenViewModel: PlansScreenViewModel
){

    val plansScreenUiState by plansScreenViewModel.plansScreenUiState.collectAsStateWithLifecycle()
    val plans = plansScreenUiState.plans.orEmpty()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        plansScreenViewModel.plansScreenEvent.collect { event ->
            when(event){
                is PlansScreenEvent.ShowSuccessMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is PlansScreenEvent.ShowErrorMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    when{
        plansScreenUiState.loading -> {
            LoadingDialog(
                message = plansScreenUiState.loadingMessage
            )
        }
        plansScreenUiState.showAddPlanDialog -> {
            AddEditPlanDialog(
                onClickAddEdit = {
                    plansScreenViewModel.addPlan()
                },
                onCancel = {
                    plansScreenViewModel.updateShowAddPlanDialog(false)
                },
                planEntry = plansScreenUiState.newPlanEntry,
                onFieldValueChange = { value, field ->
                    plansScreenViewModel.updateNewPlanEntry(value, field)
                }
            )
        }
        plansScreenUiState.showEditPlanDialog -> {
            plansScreenUiState.planEntryToUpdate?.let {
                AddEditPlanDialog(
                    onClickAddEdit = {
                        plansScreenViewModel.updatePlan()
                    },
                    onCancel = {
                        plansScreenViewModel.updateShowEditPlanDialog(false)
                    },
                    planEntry = it,
                    onFieldValueChange = { value, field ->
                        plansScreenViewModel.updatePlanEntryToUpdate(value, field)
                    },
                    isForEdit = true
                )
            }

        }
        plansScreenUiState.planIdToDelete != null -> {
            CustomAlertDialog(
                dialogTitle = "Confirm Delete",
                dialogText = "Are you sure you want to delete this plan?",
                icon = Icons.Outlined.Delete,
                onDismiss = {
                    plansScreenViewModel.setPlanToDelete(null)
                },
                onConfirm = {
                    plansScreenUiState.planIdToDelete?.let {
                        plansScreenViewModel.deletePlan(it)
                    }
                },
                iconColor = MaterialTheme.colorScheme.error
            )
        }

    }

    Scaffold (
        topBar = {
            CustomAppBar(
                title = "Plans",
                actionIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                navigationIcon = Icons.Default.ArrowBackIosNew,
                onNavigationIconClick = { navController.navigateUp()},
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    plansScreenViewModel.updateShowAddPlanDialog(true)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add Plan"
                )
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                PullToRefreshBox(
                    modifier = Modifier.weight(1f),
                    isRefreshing = plansScreenUiState.isRefreshing,
                    contentAlignment = Alignment.TopCenter,
                    onRefresh = { plansScreenViewModel.fetchPlans(isRefresh = true) }
                ) {
                    when {
                        plansScreenUiState.error != null -> {
                            ErrorUIComponent(
                                modifier = Modifier,
                                error = plansScreenUiState.error
                            )
                        }
                        plans.isNotEmpty() -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                            ) {
                                items(items = plans) { plan ->
                                    PlanCard(
                                        plan = plan,
                                        onEditPlanClick = {
                                            plansScreenViewModel.setPlanToUpdate(plan)
                                            plansScreenViewModel.updateShowEditPlanDialog(true)
                                        },
                                        onDeletePlanClick = {
                                            plansScreenViewModel.setPlanToDelete(plan.id)
                                        }
                                    )
                                }
                            }
                        }
                        else -> {
                            EmptyUIComponent(
                                modifier = Modifier,
                                message = "No Plans Found"
                            )
                        }

                    }
                }

            }
        }
    )
}

@Composable
fun PlanCard(
    plan: Plan,
    onEditPlanClick: () -> Unit,
    onDeletePlanClick: () -> Unit
){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesomeMotion,
                        contentDescription = "Day Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = plan.getNameForDisplay(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Icon(
                        imageVector = Icons.Outlined.Timelapse,
                        contentDescription = "Day Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${plan.duration_days} day(s)",
                        style = MaterialTheme.typography.bodyMedium
                    )

                }

            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically

            ){
                AssistChip(
                    onClick = {

                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Wallet,
                            contentDescription = "Info Icon",
                        )
                    },
                    label = {
                        Text(
                            text = "Price: Ksh. ${plan.price}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                )
                AssistChip(
                    onClick = {

                    },
                    enabled = plan.active,
                    label = {
                        Text(
                            text = if(plan.active) "Active" else "Inactive",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(
                    onClick = { onEditPlanClick() }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit Plan",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                IconButton(
                    onClick = { onDeletePlanClick() }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete Plan",
                        tint = MaterialTheme.colorScheme.error

                    )
                }
            }

        }
    }
}

@Composable
@Preview(widthDp = 320)
fun PlanCardPreview(){
    PlanCard(
        plan = samplePlan,
        onEditPlanClick = {},
        onDeletePlanClick = {}
    )
}