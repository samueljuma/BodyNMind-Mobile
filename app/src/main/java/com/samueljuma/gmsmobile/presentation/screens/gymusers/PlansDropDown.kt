package com.samueljuma.gmsmobile.presentation.screens.gymusers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samueljuma.gmsmobile.data.models.Plan
import com.samueljuma.gmsmobile.utils.getNameForDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansDropDown(
    modifier: Modifier,
    selectedOption: Plan,
    options: List<Plan>,
    onOptionSelected: (Plan) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 14.dp)
                .menuAnchor()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedOption.getNameForDisplay(), modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        ExposedDropdownMenu(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            matchTextFieldWidth = true,
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(text = option.getNameForDisplay()) },
                    onClick = {
                        expanded = false
                        onOptionSelected(option)
                    }
                )
                if (index < options.size - 1){
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}