package com.samueljuma.gmsmobile.presentation.screens.trainerpayments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samueljuma.gmsmobile.R
import com.samueljuma.gmsmobile.data.models.TrainerPaymentDto
import com.samueljuma.gmsmobile.utils.formatAmount
import com.samueljuma.gmsmobile.utils.formatedAsCurrency
import com.samueljuma.gmsmobile.utils.getDateFromDateTimeStamp

@Composable
fun TrainerPaymentsTable(
    trainerPayments: List<TrainerPaymentDto>,
    onEditRecord: (trainerPayment: TrainerPaymentDto) -> Unit,
    onDeleteRecord: (trainerPayment: TrainerPaymentDto) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Payment Records",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.secondary)
        PaymentsHeaderRow()
        HorizontalDivider(color = MaterialTheme.colorScheme.secondary)

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(items = trainerPayments){ trainerPayment ->
                PaymentsDataRow(
                    trainerPayment = trainerPayment,
                    onEdit = { onEditRecord(trainerPayment) },
                    onDelete = { onDeleteRecord(trainerPayment) }
                )
            }
        }
        TotalsRow(payments = trainerPayments)

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun PaymentsHeaderRow(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
    ) {
        RecordText(text = "Trainer", modifier = Modifier.weight(2f), textAlign = TextAlign.Start, fontWeight = FontWeight.Bold)
        RecordText(text = "Amount", modifier = Modifier
            .weight(1.5f)
            .padding(start = 4.dp), textAlign = TextAlign.Start, fontWeight = FontWeight.Bold)
        RecordText(text = "Date", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Start)
        RecordText(text = "Notes", modifier = Modifier.weight(2.2f), textAlign = TextAlign.Start, fontWeight = FontWeight.Bold)
        RecordText(text = "...", modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PaymentsDataRow(
    trainerPayment: TrainerPaymentDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RecordText(text = trainerPayment.trainer.full_name, modifier = Modifier.weight(2f), textAlign = TextAlign.Start)
        RecordText(text = trainerPayment.amount.formatAmount(), color = MaterialTheme.colorScheme.primary, modifier = Modifier
            .weight(1.5f)
            .padding(start = 4.dp), textAlign = TextAlign.Start)
        RecordText(text = trainerPayment.paid_at.getDateFromDateTimeStamp(), modifier = Modifier.weight(2f), textAlign = TextAlign.Start)
        RecordText(text = trainerPayment.notes, modifier = Modifier.weight(2.2f), textAlign = TextAlign.Start, fontSize = 10.sp)
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ){
            IconButton(
                modifier = Modifier.size(16.dp),
                onClick = onEdit
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = colorResource(R.color.green)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                modifier = Modifier.size(16.dp),
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(0.2f))
}

@Composable
fun TotalsRow(payments: List<TrainerPaymentDto>){
    val totalAmount = payments.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val paymentCount = payments.size

    val totals = listOf(
        "Payment Count" to paymentCount.toString(),
        "Total Amount" to "Ksh ${totalAmount.formatedAsCurrency()}"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.secondary)
        totals.forEach {(label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1.5f))
                    Text(
                        modifier = Modifier.weight(1f),
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.End
                    )
                    Text(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f),
                        text = value,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        textAlign = TextAlign.End
                    )
                }
            }

        }
        HorizontalDivider(color = MaterialTheme.colorScheme.secondary)
    }

}

@Composable
fun RecordText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign? = TextAlign.Start,
    fontSize: TextUnit = MaterialTheme.typography.bodySmall.fontSize,
    fontWeight: FontWeight = FontWeight.SemiBold,
    color: Color = MaterialTheme.colorScheme.onBackground,
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    Text(
        text = text,
        modifier = modifier.padding(vertical = 2.dp),
        textAlign = textAlign,
        color = color,
        style = style.copy(
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    )
}