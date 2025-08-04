package com.samueljuma.gmsmobile.presentation.screens.gymusers

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Phone
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.samueljuma.gmsmobile.R
import com.samueljuma.gmsmobile.domain.models.GymUser
import com.samueljuma.gmsmobile.utils.BASE_URL
import com.samueljuma.gmsmobile.utils.MenuItem
import com.samueljuma.gmsmobile.utils.UserRole
import com.samueljuma.gmsmobile.utils.getInitials

@Composable
fun GymMemberCard(
    gymUser: GymUser,
    onPaymentsClicked: () -> Unit = {},
    onMarkAttendanceClicked: () -> Unit = {},
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
){
    var expanded by  rememberSaveable { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val isMember = gymUser.role == UserRole.MEMBER.string

    val menuItems = listOf(
        MenuItem(
            label = "Mark Attendance",
            action = onMarkAttendanceClicked,
            leadingIcon = Icons.Outlined.CalendarToday
        )
    )


    Card (
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable(
                onClick = {
                    expanded = !expanded
                }
            ),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
        ) {
            Row (
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ),
                    contentAlignment = Alignment.Center
                ){

                    if(gymUser.profile_picture.isNullOrEmpty()){
                        Text(
                            text = gymUser.full_name?.getInitials() ?: "",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }else{
                        AsyncImage(
                            model = "$BASE_URL${gymUser.profile_picture}",
                            placeholder = painterResource(R.drawable.splash_logo),
                            error = painterResource(R.drawable.splash_logo),
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface)
                        )
                    }
                }

                Text(
                    text = gymUser.full_name ?: "unknown",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .weight(1f)
                )
//                if(!isMember){
                IconButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    Icon(
                        imageVector = if(expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                        contentDescription = if(expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
//                }else {
//
//                    Box {
//                        IconButton(
//                            onClick = {
//                                showMenu = !showMenu
//                            }
//                        ) {
//                            Icon(
//                                imageVector = Icons.Outlined.MoreVert,
//                                contentDescription = "More Options",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                        DropdownMenu(
//                            modifier = Modifier
//                                .wrapContentWidth(),
//                            shape = RoundedCornerShape(8.dp),
//                            expanded = showMenu,
//                            onDismissRequest = { showMenu = false },
//                            offset = DpOffset(x = 0.dp, y = (-105).dp) // adjust height as needed
//                        ) {
//                            menuItems.forEach { item ->
//                                DropdownMenuItem(
//                                    text = { Text(item.label, fontWeight = FontWeight.Bold) },
//                                    leadingIcon = {
//                                        Icon(
//                                            imageVector = item.leadingIcon,
//                                            contentDescription = "Calendar Icon",
//                                            modifier = Modifier.size(20.dp),
//                                        )
//                                    },
//                                    onClick = {
//                                        showMenu = false
//                                        item.action()
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }

                Spacer(modifier = Modifier.width(2.dp))

            }
            if(expanded){

                Spacer(modifier = Modifier.padding(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Contact Details",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .padding(bottom = 6.dp)
                        )
                        TextWithLeadingIcon(
                            text = gymUser.phone_number ?: "NA",
                            icon = Icons.Outlined.Phone
                        )
                        TextWithLeadingIcon(
                            text = gymUser.email ?: "NA",
                            icon = Icons.Outlined.Email
                        )
                    }
                    Column(
                        modifier = Modifier.weight(0.6f),
                        horizontalAlignment = Alignment.End
                        ) {
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = if(isMember)"Member Since" else "Date Joined",
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier
                                    .padding(bottom = 6.dp)
                            )
                            TextWithLeadingIcon(
                                text = "${gymUser.date_joined}",
                                icon = Icons.Outlined.CalendarMonth
                            )
                        }
                    }

                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    if(gymUser.role == UserRole.MEMBER.string){
                        IconButton(
                            onClick = { onPaymentsClicked() }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Payment,
                                contentDescription = "Payments",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    IconButton(
                        onClick = { onEditClicked() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    IconButton(
                        onClick = { onDeleteClicked() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TextWithLeadingIcon(
    text: String,
    icon : ImageVector
){
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = icon,
            contentDescription = "Icon",
            tint = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
        )
    }

}