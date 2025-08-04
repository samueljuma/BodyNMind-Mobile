package com.samueljuma.gmsmobile.presentation.screens.gymusers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samueljuma.gmsmobile.domain.models.GymUser
import com.samueljuma.gmsmobile.presentation.screens.common.CustomTextField
import com.samueljuma.gmsmobile.presentation.screens.dashboard.CustomDropDown
import com.samueljuma.gmsmobile.utils.UserRole
import com.samueljuma.gmsmobile.utils.stripCountryCode


@Composable
fun UpdateUserDialog(
    onClickUpdate: () -> Unit,
    onDismiss: () -> Unit,
    gymUsersViewModel: GymUsersViewModel,
) {

    val gymUsersUiState by gymUsersViewModel.gymUsersScreenUiState.collectAsStateWithLifecycle()
    val gymUserEntry = gymUsersUiState.gymUserEntry

    var selectedUserRole by remember { mutableStateOf(gymUserEntry.role) }

    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .verticalScroll(rememberScrollState())
                    .wrapContentHeight()
            ) {

                Spacer(modifier = Modifier.height(12.dp))

                CustomTextField(
                    value = gymUserEntry.userName,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 3.dp),
                    onValueChange = { value ->
                        gymUsersViewModel.updateGymUserEntry(
                            value = value,
                            field = GymUserField.UserName
                        )
                    },
                    placeholder = "Username",
                    isError = gymUserEntry.usernameError != null,
                    errorMessage = gymUserEntry.usernameError
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    CustomTextField(
                        value = gymUserEntry.firstName,
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            gymUsersViewModel.updateGymUserEntry(
                                value = it,
                                field = GymUserField.FirstName
                            )
                        },
                        placeholder = "First Name",
                        isError = gymUserEntry.firstNameError != null,
                        errorMessage = gymUserEntry.firstNameError
                    )
                    CustomTextField(
                        value = gymUserEntry.lastName,
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            gymUsersViewModel.updateGymUserEntry(
                                value = it,
                                field = GymUserField.LastName
                            )
                        },
                        placeholder = "Last Name",
                        isError = gymUserEntry.lastNameError != null,
                        errorMessage = gymUserEntry.lastNameError
                    )
                }


                CustomTextField(
                    value = gymUserEntry.email,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 3.dp),
                    onValueChange = {
                        gymUsersViewModel.updateGymUserEntry(
                            value = it,
                            field = GymUserField.Email
                        )
                    },
                    placeholder = "Email",
                    keyboardType = KeyboardType.Email,
                    isError = gymUserEntry.emailError != null,
                    errorMessage = gymUserEntry.emailError,
                    supportingText = "Optional Field*"
                )
                CustomTextField(
                    value = gymUserEntry.phoneNumber.stripCountryCode(),
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 3.dp),
                    onValueChange = {
                        gymUsersViewModel.updateGymUserEntry(
                            value = it,
                            field = GymUserField.PhoneNumber
                        )
                    },
                    prefix = "+254",
                    placeholder = "Phone Number",
                    keyboardType = KeyboardType.Phone,
                    isError = gymUserEntry.phoneNumberError != null,
                    errorMessage = gymUserEntry.phoneNumberError,
                    supportingText = "Optional Field*"
                )

                CustomDropDown(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 3.dp, vertical = 3.dp),
                    selectedOption = selectedUserRole ?: UserRole.MEMBER.string,
                    options = UserRole.entries.map { it.string },
                    onOptionSelected = {
                        selectedUserRole = it
                        gymUsersViewModel.updateGymUserRole(it)
                    }
                )

                // Cancel Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.padding(6.dp))
                    TextButton(
                        onClick = onClickUpdate,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Update",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
