package com.samueljuma.gmsmobile.presentation.screens.dashboard


import com.samueljuma.gmsmobile.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.samueljuma.gmsmobile.domain.models.UserDomain
import com.samueljuma.gmsmobile.utils.BASE_URL


@Composable
fun DashBoardDrawer(
    userDomain: UserDomain,
    onItemSelected: (String) -> Unit
) {
    val drawerWidth = LocalConfiguration.current.screenWidthDp.dp * 0.75f
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(drawerWidth)
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
    ) {
        ProfileHeader(
            userDomain = userDomain
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val drawerItemLabels =
                if(userDomain.role == "Admin") DrawerItemLabels.entries.toTypedArray()
                else DrawerItemLabels.entries.filter { !it.isAdminFeature }.toTypedArray()

            drawerItemLabels.forEach {
                DrawerItem(
                    icon = it.icon,
                    label = it.label,
                    onItemClick = {
                        onItemSelected(it.label)}
                )
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            // App Version at the bottom
            Text(
                text = "App Version 1.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(16.dp)
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 16.dp)
                    .systemBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ){

                Text(
                    text = "Powered by",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp
                    ),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PhillQins Hub Ltd",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Blue,
                        fontSize = 10.sp
                    ),
                )
            }

        }
    }

}

@Composable
fun DrawerItem(
    icon: Int,
    label: String,
    onItemClick: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = label,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

        }
        Icon(
            modifier = Modifier.padding(end = 16.dp)
                .size(20.dp),
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ProfileHeader(
    userDomain : UserDomain,
){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface
                ),
        ){
            AsyncImage(
                model = "${BASE_URL}${userDomain.profile_picture}",
                placeholder = painterResource(R.drawable.splash_logo),
                error = painterResource(R.drawable.splash_logo),
                modifier = Modifier.fillMaxSize()
                    .clip(CircleShape),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min), // forces Row to use the height of its tallest child
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = userDomain.role ?: "unknown",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Vertical divider
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight() //make divider fill Row's height
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column{
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = userDomain.username ?: "unknown",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = userDomain.email ?: "example@gmail.com",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }



    }
}

enum class DrawerItemLabels(val label: String, val icon: Int, val isAdminFeature: Boolean = false) {
    ADD_USER("Add User", R.drawable.trainer_add),
    PLANS("Plans", R.drawable.plan_ic),
    MARK_ATTENDANCE("Mark Attendance", R.drawable.mark_attendance_ic),
    TRAINER_PAYMENTS("Trainer Payments", R.drawable.trainer_payments_ic, isAdminFeature = true),
    GYM_EXPENSES("Gym Expenses", R.drawable.expenses_ic, isAdminFeature = true),
    LOGOUT("Logout", R.drawable.logout_ic),
}