package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.location.TransitLocationState
import com.example.data.model.CrowdReport
import com.example.data.model.StationPrediction
import com.example.data.model.TrainDirection
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    locationState: TransitLocationState,
    isOnline: Boolean,
    pendingOfflineCount: Int,
    recentReports: List<CrowdReport>,
    predictions: List<StationPrediction>,
    isLoadingPredictions: Boolean,
    isSubmittingReport: Boolean,
    onRefreshPredictions: () -> Unit,
    onSubmitReport: (CrowdReport) -> Unit,
    onSignOutClicked: () -> Unit,
    userDisplayName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black) // Pure bg-black
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Bar (Pure black, flat border-b border-gray-800)
            DashboardHeader(
                userDisplayName = userDisplayName,
                isOnline = isOnline,
                pendingOfflineCount = pendingOfflineCount,
                onSignOutClicked = onSignOutClicked
            )

            // Scrollable content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(top = 0.dp, bottom = 120.dp)
            ) {
                // 1. Live GPS Telemetry Card (Flat pure black with border-b border-gray-800)
                item {
                    LiveTelemetrySection(locationState = locationState)
                }

                // 2. Gemini Prediction Engine Section (Fixed list: Station Name + Density Badge, NO conversational advice)
                item {
                    GeminiPredictionSection(
                        predictions = predictions,
                        isLoading = isLoadingPredictions,
                        currentStation = locationState.nearestStation.name,
                        direction = locationState.direction,
                        onRefresh = onRefreshPredictions
                    )
                }

                // 3. Live Swarm Feed Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .border(1.dp, Color(0xFF1F2937), RectangleShape)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE SWARM REPORTS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White
                        )

                        Text(
                            text = "${recentReports.size} active",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF06B6D4)
                        )
                    }
                }

                // 4. Reports List (Flat border-b border-gray-800 rows)
                if (recentReports.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black)
                                .border(1.dp, Color(0xFF1F2937), RectangleShape)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No live reports yet. Tap a button below to report crowding.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                } else {
                    items(recentReports.take(15)) { report ->
                        ReportItemRow(report = report)
                    }
                }
            }
        }

        // Fixed Bottom SWARM CROWD REPORT Bar (Full bleed, h-24 buttons, zero gaps/margins)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            CrowdReporter(
                locationState = locationState,
                isSubmitting = isSubmittingReport,
                onReportSubmitted = onSubmitReport
            )
        }
    }
}

@Composable
fun DashboardHeader(
    userDisplayName: String,
    isOnline: Boolean,
    pendingOfflineCount: Int,
    onSignOutClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .border(1.dp, Color(0xFF1F2937), RectangleShape) // border-b border-gray-800
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Train,
                contentDescription = null,
                tint = Color(0xFF06B6D4),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "TRANSIT SAATHI",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = userDisplayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9CA3AF)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .background(if (isOnline) Color(0xFF16A34A) else Color(0xFFCA8A04))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (isOnline) "ONLINE" else if (pendingOfflineCount > 0) "PARSIK ($pendingOfflineCount)" else "OFFLINE",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Black),
                    color = Color.White
                )
            }

            IconButton(
                onClick = onSignOutClicked,
                modifier = Modifier.testTag("sign_out_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Sign Out",
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun LiveTelemetrySection(
    locationState: TransitLocationState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .border(1.dp, Color(0xFF1F2937), RectangleShape)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF06B6D4),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "GEOFENCE DETECTED",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF06B6D4)
                )
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF1F2937))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${locationState.nearestStation.line} Line",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = locationState.nearestStation.name.uppercase(),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFF1F2937), thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FlatMetric(
                label = "SPEED",
                value = "${locationState.speedKmh} km/h"
            )
            FlatMetric(
                label = "DIRECTION",
                value = when (locationState.direction) {
                    TrainDirection.UP -> "UP (South)"
                    TrainDirection.DOWN -> "DOWN (North)"
                    TrainDirection.STATIONARY -> "Stationary"
                    TrainDirection.UNKNOWN -> "Unknown"
                }
            )
            FlatMetric(
                label = "NEXT INTERCEPT",
                value = locationState.nextStationPredicted?.name ?: "Detecting"
            )
        }
    }
}

@Composable
fun FlatMetric(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
            color = Color(0xFF6B7280)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}

@Composable
fun GeminiPredictionSection(
    predictions: List<StationPrediction>,
    isLoading: Boolean,
    currentStation: String,
    direction: TrainDirection,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .border(1.dp, Color(0xFF1F2937), RectangleShape)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF1F2937), RectangleShape)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFEAB308),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GEMINI PREDICTIVE ENGINE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
            }

            IconButton(
                onClick = onRefresh,
                modifier = Modifier.size(24.dp).testTag("refresh_gemini_prediction_btn")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color(0xFFEAB308),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Strict List Format:
        // <div class="flex justify-between border-b border-gray-800 p-4">
        //   <span class="font-bold text-white">Station Name</span>
        //   <span class="bg-red-600 text-white px-2 py-1">Super Dense Crush</span>
        // </div>
        if (predictions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Calculating station crowding...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )
            }
        } else {
            predictions.forEach { pred ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF1F2937), RectangleShape)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pred.stationName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    val badgeBg = when (pred.predictedDensity) {
                        "GREEN_SEATS" -> Color(0xFF16A34A) // bg-green-600
                        "YELLOW_STAND" -> Color(0xFFCA8A04) // bg-yellow-600
                        else -> Color(0xFFDC2626) // bg-red-600
                    }

                    val badgeLabel = when (pred.predictedDensity) {
                        "GREEN_SEATS" -> "Not Crowded"
                        "YELLOW_STAND" -> "Moderately Crowded"
                        else -> "Super Dense Crush"
                    }

                    Box(
                        modifier = Modifier
                            .background(badgeBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = badgeLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportItemRow(report: CrowdReport) {
    val badgeBg = when (report.crowdLevel) {
        "GREEN_SEATS" -> Color(0xFF16A34A)
        "YELLOW_STAND" -> Color(0xFFCA8A04)
        else -> Color(0xFFDC2626)
    }

    val badgeLabel = when (report.crowdLevel) {
        "GREEN_SEATS" -> "Not Crowded"
        "YELLOW_STAND" -> "Moderately Crowded"
        else -> "Super Dense Crush"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .border(1.dp, Color(0xFF1F2937), RectangleShape)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = report.nearestStation,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = "${report.line} • ${report.crowdCategory} • ${report.direction}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9CA3AF)
            )
        }

        Box(
            modifier = Modifier
                .background(badgeBg)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = badgeLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = Color.White
            )
        }
    }
}
