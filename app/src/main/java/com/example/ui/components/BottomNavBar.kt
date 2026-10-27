package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class TransitTab(val title: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, "tab_dashboard"),
    TIMETABLE("Timetable", Icons.Default.Schedule, "tab_timetable"),
    ROUTE_OPTIMIZER("Optimizer", Icons.AutoMirrored.Filled.AltRoute, "tab_optimizer"),
    SOS("SOS", Icons.Default.Shield, "tab_sos")
}

@Composable
fun TransitBottomBar(
    selectedTab: TransitTab,
    onTabSelected: (TransitTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .border(1.dp, Color(0xFF1F2937), RectangleShape),
        containerColor = Color.Black,
        tonalElevation = 0.dp
    ) {
        TransitTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            val tabColor = when (tab) {
                TransitTab.DASHBOARD -> Color(0xFF06B6D4) // cyan-500
                TransitTab.TIMETABLE -> Color(0xFF22C55E) // green-500
                TransitTab.ROUTE_OPTIMIZER -> Color(0xFFEAB308) // yellow-500
                TransitTab.SOS -> Color(0xFFDC2626) // red-600
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.title.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                            letterSpacing = 0.5.sp
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = tabColor,
                    selectedTextColor = tabColor,
                    unselectedIconColor = Color(0xFF6B7280),
                    unselectedTextColor = Color(0xFF6B7280),
                    indicatorColor = Color.Black
                ),
                modifier = Modifier.testTag(tab.tag)
            )
        }
    }
}
