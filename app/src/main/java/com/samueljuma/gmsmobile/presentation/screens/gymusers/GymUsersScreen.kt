package com.samueljuma.gmsmobile.presentation.screens.gymusers

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samueljuma.gmsmobile.domain.filterByQuery
import com.samueljuma.gmsmobile.domain.toGymUserEntryDto
import com.samueljuma.gmsmobile.presentation.navigation.AppScreens
import com.samueljuma.gmsmobile.presentation.screens.common.CustomAlertDialog
import com.samueljuma.gmsmobile.presentation.screens.common.CustomAppBar
import com.samueljuma.gmsmobile.presentation.screens.common.ErrorUIComponent
import com.samueljuma.gmsmobile.presentation.screens.common.LoadingDialog
import com.samueljuma.gmsmobile.presentation.screens.common.SearchField
import com.samueljuma.gmsmobile.utils.UserRole
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymUsersScreen(
    userType: String,
    onNavigationIconClick: () -> Unit,
    gymUsersViewModel: GymUsersViewModel,
    navController: NavController
){
    val gymUsersUiState by gymUsersViewModel.gymUsersScreenUiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    val filteredGymUsers = gymUsersUiState.gymUsers?.filterByQuery(searchQuery) ?: emptyList()

    var userRole by remember { mutableStateOf(UserRole.MEMBER) }

    val gymUserEntry = gymUsersUiState.gymUserEntry
    val showAddUSerDialog = gymUsersUiState.showAddUserDialog
    val context = LocalContext.current

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val maxSheetHeight = screenHeight * 0.8f

    var showMpesaConfirmationDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        gymUsersViewModel.fetchGymUsers(userType)
        //fetch plans early
        gymUsersViewModel.fetchSubscriptionPlans()
    }


    LaunchedEffect(Unit) {
        gymUsersViewModel.gymUsersScreenEvent.collect { event ->
            when(event){
                is GymUsersScreenEvent.ShowAddUserDialog -> {
                    gymUsersViewModel.updateShowAddUserDialog(true)
                    userRole = event.userRole

                    //initialize gymUserEntry with role
                    gymUsersViewModel.updateGymUserRole(event.userRole.string)
                }
                is GymUsersScreenEvent.ShowErrorMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is GymUsersScreenEvent.ShowSuccessMessage -> {
                    Toast.makeText(context, gymUsersUiState.successMessage, Toast.LENGTH_SHORT).show()
                }
                is GymUsersScreenEvent.ShowMpesaConfirmationDialog -> {
                    showMpesaConfirmationDialog = true
                }
            }
        }
    }
    when{
        gymUsersUiState.showDialogForConfirmingPayments ->{
            CustomAlertDialog(
                dialogTitle = "Proceed with Payment Processing",
                dialogText = "Are you sure you want to proceed?",
                onDismiss = {
                    gymUsersViewModel.updateShowDialogForConfirmingPayments(false)
                },
                onConfirm = {
                    val selectedPlan = gymUsersUiState.selectedPlan
                    selectedPlan?.let { plan->
                        val paymentDetails = gymUsersUiState.paymentDetails
                        paymentDetails.validate(plan)

                        gymUsersViewModel.processMemberPayment()
                        gymUsersViewModel.updateShowDialogForConfirmingPayments(false)
                        showBottomSheet = false

                    }

                },
                icon = Icons.Outlined.Done,
                iconColor = MaterialTheme.colorScheme.error,
                confirmButtonText = "Proceed"

            )

        }
        showAddUSerDialog -> {
            AddUserDialog(
                userRole = userRole,
                onDismiss = {
                    gymUsersViewModel.resetGymUserEntry()
                },
                onClickAdd = {
                    val isValid = gymUsersViewModel.validateAllFields()
                    Log.d("ADD GYM USER", "GymUsersScreen: $gymUserEntry")
                    if(isValid){
                        gymUsersViewModel.addGymUser(gymUserEntry)
//                        gymUsersViewModel.updateShowAddUserDialog(false)
                        gymUsersViewModel.resetGymUserEntry()
                    }

                },
                gymUserEntry = gymUserEntry,
                onFieldValueChange = { value, field ->
                    gymUsersViewModel.updateGymUserEntry (value, field)
                },
                onRoleChange = {
                    gymUsersViewModel.updateGymUserRole(role = it)
                }
            )
        }
        gymUsersUiState.isLoading -> {
            LoadingDialog(
                message = gymUsersUiState.loadingMessage
            )
        }
        gymUsersUiState.gymUserToDelete != null -> {
            CustomAlertDialog(
                dialogTitle = "Confirm Delete",
                dialogText = "Are you sure you want to delete this user?",
                icon = Icons.Outlined.Delete,
                onDismiss = {
                    gymUsersViewModel.setGymUserToDelete(null)
                },
                onConfirm = {
                    gymUsersUiState.gymUserToDelete?.let { member ->
                        gymUsersViewModel.deleteGymUser(member)
                    }
                },
                iconColor = MaterialTheme.colorScheme.error
            )
        }

        gymUsersUiState.gymUserToUpdate != null -> {
            gymUsersUiState.gymUserToUpdate?.let { gymUser->
                UpdateUserDialog(
                    onClickUpdate = {
                        val isValid = gymUsersViewModel.validateAllFields()
                        if(isValid){
                            gymUsersViewModel.updateGymUser(
                                userId =gymUser.id,
                                gymUser = gymUserEntry.toGymUserEntryDto()
                            )
                        }
                    },
                    onDismiss = {
                        gymUsersViewModel.setGymUserToUpdate(null)
                    },
                    gymUsersViewModel
                )
            }

        }
        showBottomSheet -> {
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(),
                onDismissRequest = {
                    showBottomSheet = false

                    // Reset selected plan
                    gymUsersUiState.subscriptionPlans?.get(0)?.let {
                        gymUsersViewModel.updateSelectedPlan(
                            plan = it
                        )
                    }
                },
                contentWindowInsets = { WindowInsets.systemBars }, // prevents resize
                sheetState = sheetState
            ) {
                // Sheet content
                    PaymentContainer(
                        gymUsersViewModel = gymUsersViewModel,
                        onClickPay = {
                            val selectedPlan = gymUsersUiState.selectedPlan
                            selectedPlan?.let { plan->
                                val paymentDetails = gymUsersUiState.paymentDetails
                                paymentDetails.validate(plan)

                                gymUsersViewModel.updateShowDialogForConfirmingPayments(true)
                                showBottomSheet = false
                            }
                        }
                    )
            }
        }

        showMpesaConfirmationDialog -> {
            CustomAlertDialog(
                dialogTitle = "Mpesa Payment Confirmation",
                dialogText = "Check client Message and Click Confirm",
                icon = Icons.Outlined.Info,
                onDismiss = {
                    showMpesaConfirmationDialog = false
                },
                onConfirm = {
                    showMpesaConfirmationDialog = false
                    gymUsersViewModel.confirmMpesaPayment()
                },
                iconColor = MaterialTheme.colorScheme.primary
            )
        }

    }

    var showMorevetMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomAppBar(
                title = if(userType == UserRole.MEMBER.string) "Members" else "Trainers",
                navigationIcon = Icons.Default.ArrowBackIosNew,
                onActionIconClick = {
                    if(userType != UserRole.MEMBER.string){
                        navController.navigateUp()
                    }

                },
                onNavigationIconClick = onNavigationIconClick,
                hasMoreActions = userType == UserRole.MEMBER.string,
                showMenu = showMorevetMenu,
                onDismiss = {showMorevetMenu = false},
                menuItems = listOf(
                    "Confirm Mpesa Payments" to {
                        Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                    },
                    "Mark Attendance" to {
                        navController.navigate(AppScreens.MarkAttendance.route){
                            popUpTo(AppScreens.DashBoard.route){
                                inclusive = false
                            }
                        }
                    }
                ),
                actionIcon = {
                    IconButton(
                        onClick = {
                            if(userType != UserRole.MEMBER.string){
                                navController.navigateUp()
                            }else{
                                showMorevetMenu = !showMorevetMenu
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if(userType == UserRole.MEMBER.string) Icons.Outlined.MoreVert else Icons.Outlined.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        gymUsersViewModel.onAddUserClicked(
                            userRole = if(userType == UserRole.MEMBER.string) UserRole.MEMBER else UserRole.TRAINER
                        )
                    }
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                gymUsersUiState.gymUsers?.let {
                    SearchField(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 10.dp, start = 8.dp, end = 8.dp),
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onClearClick = { searchQuery = "" }
                    )
                }

                PullToRefreshBox(
                    modifier = Modifier.weight(1f)
                        .padding(horizontal = 10.dp),
                    isRefreshing = gymUsersUiState.isRefreshing,
                    contentAlignment = Alignment.TopCenter,
                    onRefresh = { gymUsersViewModel.fetchGymUsers(userType, isRefresh = true) }
                ) {
                    when {
                        gymUsersUiState.error != null -> {
                            ErrorUIComponent(
                                modifier = Modifier,
                                error = gymUsersUiState.error
                            )
                        }
                        gymUsersUiState.gymUsers?.isNotEmpty() == true -> {

                                LazyColumn(
                                    modifier = Modifier.fillMaxSize()
                                        .padding(top = 16.dp)
                                ) {

                                    itemsIndexed(filteredGymUsers) { index, gymUser ->
                                        GymMemberCard(
                                            gymUser = gymUser,
                                            onPaymentsClicked = {
                                                showBottomSheet = true
                                                gymUsersViewModel.setGymUserToProcessPaymentsFor(gymUser)
                                            },
                                            onEditClicked = {
                                                gymUsersViewModel.setGymUserToUpdate(gymUser)
                                                gymUsersViewModel.updateGymUserEntry(gymUser)
                                            },
                                            onDeleteClicked = {
                                                gymUsersViewModel.setGymUserToDelete(gymUser)
                                            }
                                        )
                                        if (index == filteredGymUsers.lastIndex) {
                                            Spacer(modifier = Modifier.height(80.dp))
                                        }

                                    }
                                }
                        }
                    }
                }
            }

        }
    )
}