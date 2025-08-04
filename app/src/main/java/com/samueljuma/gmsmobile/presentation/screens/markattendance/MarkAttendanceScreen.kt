package com.samueljuma.gmsmobile.presentation.screens.markattendance

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samueljuma.gmsmobile.R
import com.samueljuma.gmsmobile.domain.models.MemberAttendanceDomain
import com.samueljuma.gmsmobile.presentation.screens.common.LoadingDialog
import com.samueljuma.gmsmobile.presentation.screens.common.SearchField
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkAttendanceScreen(
    navController: NavController,
    markAttendanceViewModel: MarkAttendanceViewModel
){
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var showSearchField by remember { mutableStateOf(false) }

    val uiState by markAttendanceViewModel.uiState.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val attendanceList = uiState.attendanceList.filter { it.member_name.contains(searchQuery, ignoreCase = true) }

    when {
        uiState.isLoading -> {
            LoadingDialog(
                message = uiState.loadingMessage ?: "Loading..."
            )
        }
    }

    LaunchedEffect(Unit) {
        markAttendanceViewModel.event.collect { event ->
            when (event) {
                is MarkAttendanceEvent.ShowErrorMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is MarkAttendanceEvent.ShowSuccessMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    when {
        uiState.isLoading -> {
            LoadingDialog(
                message = uiState.loadingMessage ?: "Loading..."
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mark Attendance",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Row {
                        IconButton(
                            onClick = {
                                showSearchField = !showSearchField
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Close"
                            )

                        }
                        IconButton(
                            onClick = {
                                markAttendanceViewModel.markMembersAttendance()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Close"
                            )

                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = {paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if(showSearchField) {
                    Box(
                        Modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(bottom = 10.dp)
                    ){
                        SearchField(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            query = searchQuery,
                            searchString = "Search Member...",
                            onQueryChange = {
                                searchQuery = it
                            },
                            onClearClick = {
                                searchQuery = ""
                            }
                        )
                    }
                }

                Calendar(
                    selectedDate = selectedDate,
                    onDateSelected = { date ->
                        selectedDate = date

                        // Re-fetch attendance
                        markAttendanceViewModel.fetchMembersAttendanceList(date.toString())

                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn {
                    items(items = attendanceList){ memberAttendance ->
                        MemberCardForAttendance(
                            memberAttendance = memberAttendance,
                            onCheckedChange = {
                                markAttendanceViewModel.updateMemberCheckedState(
                                    id = memberAttendance.member_id,
                                    isChecked = it
                                )
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }


            }

        }
    )
}

@Composable
fun MemberCardForAttendance(
    modifier: Modifier = Modifier,
    memberAttendance: MemberAttendanceDomain,
    onCheckedChange: (Boolean) -> Unit
){
    Card(
        modifier = modifier.fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                modifier = Modifier.padding(16.dp),
                text = memberAttendance.member_name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            if(memberAttendance.present){
                Icon(
                    modifier = Modifier.padding(end = 16.dp),
                    imageVector = Icons.Default.HowToReg,
                    contentDescription = "Present",
                    tint = colorResource(R.color.green)
                )
            }else{
                Checkbox(
                    checked = memberAttendance.isChecked,
                    onCheckedChange = onCheckedChange
                )
            }

        }
    }

}

