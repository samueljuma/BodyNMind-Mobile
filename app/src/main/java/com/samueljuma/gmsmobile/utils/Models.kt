package com.samueljuma.gmsmobile.utils

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val label: String,
    val action: () -> Unit,
    val leadingIcon: ImageVector
)