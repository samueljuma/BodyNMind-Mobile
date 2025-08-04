package com.samueljuma.gmsmobile.presentation.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.samueljuma.gmsmobile.R

@Composable
fun ErrorUIComponent(
    modifier: Modifier = Modifier,
    error: String? = null
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Image(
            modifier = Modifier.size(80.dp),
            painter = painterResource(id = R.drawable.gms_logo_captionless),
            contentDescription = "Error Icon",
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.error
            )
        )
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = error ?: "Oops! An Error Occurred",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.error
            ),
            textAlign = TextAlign.Center
        )
    }
}