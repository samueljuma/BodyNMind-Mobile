package com.samueljuma.gmsmobile.presentation.screens.plans

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.samueljuma.gmsmobile.domain.models.PlanEntry
import com.samueljuma.gmsmobile.presentation.screens.common.CustomTextField
import com.samueljuma.gmsmobile.presentation.screens.dashboard.CustomDropDown
import com.samueljuma.gmsmobile.presentation.screens.gymusers.GymUserField


@Composable
fun AddEditPlanDialog(
    onClickAddEdit: () -> Unit,
    onCancel: () -> Unit,
    planEntry: PlanEntry,
    onFieldValueChange: (value: String, field: String) -> Unit,
    isForEdit: Boolean = false
) {

    Dialog(
        onDismissRequest = { onCancel() }
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
                CustomDropDown(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 3.dp),
                    selectedOption = planEntry.name.ifBlank { "daily" },
                    options = listOf("daily", "monthly", "custom"),
                    onOptionSelected = {
                        onFieldValueChange(it, "name")
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))

                CustomTextField(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 3.dp),
                    value = planEntry.price,
                    onValueChange = {
                        onFieldValueChange(it, "price")
                    },
                    placeholder = "Enter price",
                    errorMessage = planEntry.priceError,
                    isError = planEntry.priceError != null,
                    keyboardType = KeyboardType.Number
                )

                CustomTextField(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 3.dp),
                    value = planEntry.duration_days,
                    onValueChange = {
                        onFieldValueChange(it, "duration")
                    },
                    placeholder = "Enter duration",
                    errorMessage = planEntry.durationError,
                    isError = planEntry.durationError != null,
                    keyboardType = KeyboardType.Number
                )

                // Active?
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ){

                    Checkbox(
                        checked = planEntry.active,
                        onCheckedChange = {
                            onFieldValueChange(it.toString(), "active")
                        }
                    )
                    Text(
                        text = "Active",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }


                // Cancel Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onCancel,
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
                        onClick = onClickAddEdit,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if(isForEdit) "Update" else "Add",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
