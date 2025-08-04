package com.samueljuma.gmsmobile.presentation.screens.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samueljuma.gmsmobile.domain.DropdownComponent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomChip(
    modifier: Modifier = Modifier,
    dropdownComponent: DropdownComponent
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(dropdownComponent.selectedOption) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        AssistChip(
            onClick = { },
            modifier = Modifier
                .menuAnchor(),
            shape = RoundedCornerShape(4.dp),
            label = {
                Text(
                    text = selectedOption.label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown Icon",
                    tint = Color.Black
                )
            },
        )
        ExposedDropdownMenu(
            modifier = Modifier
                .wrapContentWidth(),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            dropdownComponent.options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.label) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                        dropdownComponent.onOptionSelected(selectedOption)
                    }
                )
            }

        }
    }
}