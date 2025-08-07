package com.samueljuma.gmsmobile.presentation.screens.expenses

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.samueljuma.gmsmobile.domain.models.Category
import com.samueljuma.gmsmobile.domain.models.Expense
import com.samueljuma.gmsmobile.domain.models.Trainer
import com.samueljuma.gmsmobile.presentation.screens.common.CustomTextField
import com.samueljuma.gmsmobile.presentation.screens.dashboard.CustomDropDown


@Composable
fun ExpensesDialog(
    expense: Expense,
    onDismiss: () -> Unit,
    onSaveRecord: () -> Unit,
    onFieldChange: (field: String, value: String) -> Unit,
    categories: List<Category>
){
    Dialog(
        onDismissRequest = onDismiss
    ){
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                CustomDropDown(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 3.dp, vertical = 3.dp),
                    selectedOption = expense.category.name,
                    options = categories.map { it.name },
                    onOptionSelected = {
                        onFieldChange("category", it)
                    }
                )
                CustomTextField(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 3.dp, vertical = 3.dp),
                    value = expense.name,
                    onValueChange = {
                        onFieldChange("name", it)
                    },
                    keyboardType = KeyboardType.Text,
                    placeholder = "Name",
                    isError = expense.nameError != null,
                    errorMessage = expense.nameError
                )
                CustomTextField(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 3.dp, vertical = 3.dp),
                    value = expense.amount,
                    onValueChange = {
                        onFieldChange("amount", it)
                    },
                    keyboardType = KeyboardType.Number,
                    placeholder = "Amount",
                    isError = expense.amountError != null,
                    errorMessage = expense.amountError
                )
                CustomTextField(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 3.dp, vertical = 3.dp),
                    value = expense.notes,
                    onValueChange = {
                        onFieldChange("notes", it)
                    },
                    isError = expense.notesError != null,
                    errorMessage = expense.notesError,
                    maxLines = 3,
                    placeholder = "Notes",
                    supportingText = "Optional"
                )


                // Cancel and Save Button
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
                        onClick = onSaveRecord,
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Save Record",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }

}