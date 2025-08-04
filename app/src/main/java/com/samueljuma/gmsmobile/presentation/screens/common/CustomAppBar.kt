package com.samueljuma.gmsmobile.presentation.screens.common

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAppBar(
    title: String,
    navigationIcon: ImageVector? = null,
    actionIcon: (@Composable () -> Unit)? = null,
    onNavigationIconClick: (() -> Unit)? = null,
    onActionIconClick: (() -> Unit)? = null,
    hasMoreActions: Boolean = false,
    menuItems: List<Pair<String, () -> Unit>> = emptyList(),
    showMenu: Boolean =false,
    onDismiss: () -> Unit = {},
) {

    TopAppBar(
        title = { Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        ) },
        navigationIcon = {
            navigationIcon?.let {
                IconButton(onClick = {
                    onNavigationIconClick?.invoke()
                }) {
                    Icon(
                        imageVector = it,
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            actionIcon?.let {
                Box(
                    modifier = Modifier.clip(CircleShape)
                ){
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                onActionIconClick?.invoke()
                        }
                    ) {
                        actionIcon()
                    }

                    if (hasMoreActions) {
                        DropdownMenu(
                            modifier = Modifier
//                                .width(220.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            expanded = showMenu,
                            onDismissRequest = { onDismiss() }
                        ) {
                            menuItems.forEach { (label, action) ->
                                DropdownMenuItem(
                                    text = { Text(
                                        text = label,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    ) },
                                    onClick = {
                                        onDismiss()
                                        action()
                                    }
                                )
                            }
                        }
                    }
                }
            }

        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
