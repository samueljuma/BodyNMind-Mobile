package com.samueljuma.gmsmobile.presentation.screens.trainerpayments

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
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.samueljuma.gmsmobile.domain.models.Trainer
import com.samueljuma.gmsmobile.domain.models.TrainerPayment
import com.samueljuma.gmsmobile.presentation.screens.common.CustomTextField
import com.samueljuma.gmsmobile.presentation.screens.dashboard.CustomDropDown

@Composable
fun TrainerPaymentDetailsDialog(
    trainerPayment: TrainerPayment,
    onDismiss: () -> Unit,
    onSaveRecord: () -> Unit,
    onFieldChange: (field: String, value: String) -> Unit,
    trainers: List<Trainer>
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
                    selectedOption = trainerPayment.trainer.fullName,
                    options = trainers.map { it.fullName },
                    onOptionSelected = {
                        onFieldChange("trainer", it)
                    }
                )
                CustomTextField(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 3.dp, vertical = 3.dp),
                    value = trainerPayment.amount,
                    onValueChange = {
                        onFieldChange("amount", it)
                    },
                    keyboardType = KeyboardType.Number,
                    placeholder = "Amount",
                    isError = trainerPayment.amountError != null,
                    errorMessage = trainerPayment.amountError
                )
                CustomTextField(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 3.dp, vertical = 3.dp),
                    value = trainerPayment.notes,
                    onValueChange = {
                        onFieldChange("notes", it)
                    },
                    isError = trainerPayment.notesError != null,
                    errorMessage = trainerPayment.notesError,
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