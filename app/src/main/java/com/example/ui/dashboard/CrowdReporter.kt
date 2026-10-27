package com.example.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.location.TransitLocationState
import com.example.data.model.CrowdCategory
import com.example.data.model.CrowdDensity
import com.example.data.model.CrowdReport
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CrowdReporter(
    locationState: TransitLocationState,
    isSubmitting: Boolean,
    onReportSubmitted: (CrowdReport) -> Unit,
    modifier: Modifier = Modifier
) {
    var lastSubmittedLevel by remember { mutableStateOf<String?>(null) }
    var showSuccessToast by remember { mutableStateOf(false) }

    LaunchedEffect(showSuccessToast) {
        if (showSuccessToast) {
            delay(2000)
            showSuccessToast = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        // Feedback toast (Flat pure black with cyan border)
        AnimatedVisibility(
            visible = showSuccessToast,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .border(1.dp, Color(0xFF1F2937), RectangleShape)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF22C55E),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Report logged: ${lastSubmittedLevel?.replace("_", " ")} at ${locationState.nearestStation.name}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }

        // Section Title & Context Tag (Flat border-t border-gray-800)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .border(1.dp, Color(0xFF1F2937), RectangleShape)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SWARM CROWD REPORT",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = Color.White
            )

            Text(
                text = if (locationState.crowdCategory == CrowdCategory.TRAIN)
                    "ON TRAIN • ${locationState.speedKmh} km/h • ${locationState.direction}"
                else
                    "ON PLATFORM • ${locationState.nearestStation.name}",
                color = if (locationState.crowdCategory == CrowdCategory.TRAIN) Color(0xFF06B6D4) else Color(0xFFEAB308),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Maximize: Three massive full-bleed buttons, h-24 (96dp), no margins or gaps
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp), // h-24 full bleed touch targets
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // SEATS: Not Crowded (Green)
            FullBleedCrowdButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .testTag("crowd_btn_green"),
                label = "SEATS",
                subLabel = "Not Crowded",
                icon = Icons.Default.EventSeat,
                backgroundColor = if (lastSubmittedLevel == "GREEN_SEATS") Color(0xFF15803D) else Color(0xFF16A34A), // green-600
                textColor = Color.White,
                enabled = !isSubmitting,
                onClick = {
                    lastSubmittedLevel = "GREEN_SEATS"
                    showSuccessToast = true
                    onReportSubmitted(createReport(locationState, CrowdDensity.GREEN_SEATS))
                }
            )

            // STAND: Moderately Crowded (Yellow)
            FullBleedCrowdButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(width = 1.dp, color = Color.Black, shape = RectangleShape)
                    .testTag("crowd_btn_yellow"),
                label = "STAND",
                subLabel = "Moderate",
                icon = Icons.Default.Group,
                backgroundColor = if (lastSubmittedLevel == "YELLOW_STAND") Color(0xFFA16207) else Color(0xFFCA8A04), // yellow-600
                textColor = Color.White,
                enabled = !isSubmitting,
                onClick = {
                    lastSubmittedLevel = "YELLOW_STAND"
                    showSuccessToast = true
                    onReportSubmitted(createReport(locationState, CrowdDensity.YELLOW_STAND))
                }
            )

            // CRUSH: Packed (Red)
            FullBleedCrowdButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .testTag("crowd_btn_red"),
                label = "CRUSH",
                subLabel = "Packed",
                icon = Icons.Default.Warning,
                backgroundColor = if (lastSubmittedLevel == "RED_CRUSH") Color(0xFFB91C1C) else Color(0xFFDC2626), // red-600
                textColor = Color.White,
                enabled = !isSubmitting,
                onClick = {
                    lastSubmittedLevel = "RED_CRUSH"
                    showSuccessToast = true
                    onReportSubmitted(createReport(locationState, CrowdDensity.RED_CRUSH))
                }
            )
        }
    }
}

@Composable
fun FullBleedCrowdButton(
    modifier: Modifier = Modifier,
    label: String,
    subLabel: String,
    icon: ImageVector,
    backgroundColor: Color,
    textColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(backgroundColor)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = textColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = subLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = textColor.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun createReport(locationState: TransitLocationState, density: CrowdDensity): CrowdReport {
    return CrowdReport(
        latitude = locationState.latitude,
        longitude = locationState.longitude,
        nearestStation = locationState.nearestStation.name,
        line = locationState.nearestStation.line,
        speedKmh = locationState.speedKmh,
        direction = locationState.direction.name,
        crowdLevel = density.name,
        crowdCategory = locationState.crowdCategory.name
    )
}
