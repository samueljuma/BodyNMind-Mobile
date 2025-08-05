package com.samueljuma.gmsmobile.presentation.screens.gymusers

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samueljuma.gmsmobile.R
import com.samueljuma.gmsmobile.domain.models.validatePhoneNumber
import com.samueljuma.gmsmobile.presentation.screens.common.CustomTextField
import com.samueljuma.gmsmobile.utils.formatted
import com.samueljuma.gmsmobile.utils.getPlanExpiryDate
import com.samueljuma.gmsmobile.utils.showDatePickerDialog
import com.samueljuma.gmsmobile.utils.toInternationalPhone

@Composable
fun PaymentContainer(
    gymUsersViewModel: GymUsersViewModel,
    onClickPay : () -> Unit = {}
){

    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current

    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.MPESA) }

    val gymUsersScreenUiState by gymUsersViewModel.gymUsersScreenUiState.collectAsStateWithLifecycle()

    val selectedPlan = gymUsersScreenUiState.selectedPlan

    var payeePhoneNumberError by remember { mutableStateOf<String?>(null) }
    val paymentRequest = gymUsersScreenUiState.paymentRequest
    val paymentDetails = gymUsersScreenUiState.paymentDetails

    val isPaymentButtonEnabled = when (selectedPaymentMethod) {
        PaymentMethod.CASH -> true
        PaymentMethod.MPESA -> paymentDetails.isValid
    }

    when {
        gymUsersScreenUiState.showDatePicker -> {
            showDatePickerDialog(
                context = context,
                onDateSelected = {
                    gymUsersViewModel.updatePlanExpirationDate(it.formatted())
                },
                onDismiss = {
                    gymUsersViewModel.updateShowDatePicker(false)
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                vertical = 4.dp,
                horizontal = 24.dp
            )
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            text = "Process Member Payments".uppercase(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Text(
            text = "Select payment method",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            PaymentMethodCard(
                iconResId = R.drawable.mpesa_pay,
                isSelected = selectedPaymentMethod == PaymentMethod.MPESA,
                onSelect = {
                    selectedPaymentMethod = PaymentMethod.MPESA
                    // Update Payment Request
                    gymUsersViewModel.updatePaymentRequest("payment_method", PaymentMethod.MPESA.string)
                }
            )
            PaymentMethodCard(
                iconResId = R.drawable.cash_pay,
                isSelected = selectedPaymentMethod == PaymentMethod.CASH,
                onSelect = {
                    selectedPaymentMethod = PaymentMethod.CASH
                    // Update Payment Request
                    gymUsersViewModel.updatePaymentRequest("payment_method", PaymentMethod.CASH.string)
                },
                iconColor = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        PlansDropDown(
            modifier = Modifier.fillMaxWidth(),
            selectedOption = selectedPlan!!,
            options = gymUsersScreenUiState.subscriptionPlans!!,
            onOptionSelected = { plan ->
                gymUsersViewModel.updateSelectedPlan(plan)
            }
        )

        Spacer(modifier = Modifier.height(4.dp))
        CustomTextField(
//            enabled = false,
            trailingIcon = {
                AdjustPlanPrice(
                    onAdd = {
                        gymUsersViewModel.adjustPlanPrice(isAdd = true)
                    },
                    onSubtract = {
                        gymUsersViewModel.adjustPlanPrice(isAdd = false)
                    }
                )
            },
            onValueChange = { value->
                gymUsersViewModel.updatePaymentDetails("amount", value)
            },
            prefix = "Ksh. ",
            value = paymentDetails.amountValue,
            isError = paymentDetails.amountError != null,
            errorMessage = paymentDetails.amountError,
            keyboardType = KeyboardType.Number
        )

        CustomTextField(
            value = gymUsersScreenUiState.planExpirationDate ?: "",
            enabled = false,
            placeholder = "Plan Expires On",
            trailingIcon = {
                IconButton(
                    onClick = {
                        gymUsersViewModel.updateShowDatePicker(true)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = "Reduce",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(4.dp))

        AnimatedVisibility(visible = selectedPaymentMethod == PaymentMethod.MPESA) {
            CustomTextField(
                prefix = "+254",
                value = paymentDetails.phoneNumber,
                placeholder = "Enter phone number",
                isError = paymentDetails.phoneNumberError != null,
                errorMessage = paymentDetails.phoneNumberError,
                keyboardType = KeyboardType.Phone,
                onValueChange = { newValue ->
                    gymUsersViewModel.updatePaymentDetails("payee_phone", newValue)
                }
            )
        }

        Button(
            onClick = {
                keyboardController?.hide()
                onClickPay()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = isPaymentButtonEnabled ,
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            Text(
                text = if(selectedPaymentMethod == PaymentMethod.CASH) "Confirm Cash Payment" else "Initiate Mpesa Payment",
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun PaymentMethodCard(
    iconResId: Int,
    isSelected: Boolean,
    onSelect: () -> Unit,
    iconColor: Color? = null
) {

    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Card(
        modifier = Modifier
            .width(70.dp)
            .height(45.dp)
            .border(
                BorderStroke(1.dp, borderColor),
                RoundedCornerShape(6.dp)
            )
            .clickable {
                onSelect() // Select this card when clicked
            },
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Box(
            modifier = Modifier
                .padding(5.dp)
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(),
                colorFilter = iconColor?.let { ColorFilter.tint(it) }
            )
        }
    }
}

@Composable
fun AdjustPlanPrice(
    onAdd: () -> Unit,
    onSubtract: () -> Unit
){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(
            onClick = onSubtract,
        ) {
            Icon(
                imageVector = Icons.Outlined.Remove,
                contentDescription = "Reduce",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(
            onClick = onAdd,
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

enum class PaymentMethod(val string: String) {
    MPESA("M-Pesa"), CASH("Cash")
}
