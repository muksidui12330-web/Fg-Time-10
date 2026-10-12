package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.viewmodel.Screen
import com.example.ui.theme.LeafAccent
import com.example.ui.theme.LeafPrimary
import com.example.ui.theme.LeafPrimaryDark

@Composable
fun FGBottomNav(
    currentScreen: com.example.ui.viewmodel.Screen,
    userAvatar: String,
    onNavigate: (com.example.ui.viewmodel.Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Home
            BottomNavItem(
                icon = if (currentScreen == com.example.ui.viewmodel.Screen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                label = "Home",
                isSelected = currentScreen == com.example.ui.viewmodel.Screen.HOME,
                testTag = "nav_home",
                onClick = { onNavigate(com.example.ui.viewmodel.Screen.HOME) }
            )

            // Explore
            BottomNavItem(
                icon = if (currentScreen == com.example.ui.viewmodel.Screen.EXPLORE) Icons.Filled.Search else Icons.Outlined.Search,
                label = "Explore",
                isSelected = currentScreen == com.example.ui.viewmodel.Screen.EXPLORE,
                testTag = "nav_explore",
                onClick = { onNavigate(com.example.ui.viewmodel.Screen.EXPLORE) }
            )

            // Create Post Button (Leaf Green Pill)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(LeafPrimary)
                    .clickable { onNavigate(com.example.ui.viewmodel.Screen.CREATE) }
                    .testTag("nav_create"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Post",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Reels
            BottomNavItem(
                icon = if (currentScreen == com.example.ui.viewmodel.Screen.REELS) Icons.Filled.SlowMotionVideo else Icons.Outlined.SlowMotionVideo,
                label = "Reels",
                isSelected = currentScreen == com.example.ui.viewmodel.Screen.REELS,
                testTag = "nav_reels",
                onClick = { onNavigate(com.example.ui.viewmodel.Screen.REELS) }
            )

            // Profile
            val isProfileSelected = currentScreen == com.example.ui.viewmodel.Screen.PROFILE
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(56.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNavigate(com.example.ui.viewmodel.Screen.PROFILE) }
                    .padding(horizontal = 8.dp)
                    .testTag("nav_profile")
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isProfileSelected) 2.dp else 1.dp,
                            color = if (isProfileSelected) LeafPrimary else Color.Transparent,
                            shape = CircleShape
                        )
                ) {
                    AsyncImage(
                        model = userAvatar,
                        contentDescription = "My Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = if (isProfileSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isProfileSelected) LeafPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(56.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) LeafPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) LeafPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
