package com.example.ui.sos

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Vibrator
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.location.TransitLocationState
import com.example.data.model.EmergencyAlert
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SosScreen(
    locationState: TransitLocationState,
    onTriggerEmergency: (EmergencyAlert, (String) -> Unit) -> Unit,
    onCancelEmergency: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var isCountingDown by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableStateOf(3) }
    var activeEmergencyId by remember { mutableStateOf<String?>(null) }
    var emergencyStatus by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isCountingDown) {
        if (isCountingDown) {
            countdownSeconds = 3
            vibrateDevice(context, 100)
            while (countdownSeconds > 0) {
                delay(1000)
                countdownSeconds -= 1
                vibrateDevice(context, 150)
            }
            isCountingDown = false
            triggerEmergencyNow(
                context = context,
                locationState = locationState,
                onTriggerEmergency = { alert ->
                    onTriggerEmergency(alert) { id ->
                        activeEmergencyId = id
                        emergencyStatus = "EMERGENCY BROADCASTED TO RPF / GRP • INTERCEPT POINT LOGGED"
                    }
                }
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black) // Pure bg-black
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 0.dp, bottom = 40.dp)
        ) {
            // Header (Flat border-b border-gray-800)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .border(1.dp, Color(0xFF1F2937), RectangleShape)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "PREDICTIVE SOS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Instant 112 dispatch with trajectory intercept",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }

            // Countdown Banner if active
            if (isCountingDown) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF7F1D1D)) // red-950
                            .border(2.dp, Color(0xFFDC2626), RectangleShape)
                            .padding(20.dp)
                            .testTag("sos_countdown_card"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "EMERGENCY SOS TRIGGERING IN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "$countdownSeconds",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 72.sp
                            ),
                            color = Color.White
                        )

                        Text(
                            text = "Dialing 112 & broadcasting coordinates to ${locationState.nearestStation.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFCA5A5),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                isCountingDown = false
                                vibrateDevice(context, 50)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            ),
                            shape = RectangleShape,
                            modifier = Modifier
                                .border(1.dp, Color.White, RectangleShape)
                                .testTag("cancel_sos_countdown_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFDC2626))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CANCEL COUNTDOWN", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Active Alert Banner
            if (activeEmergencyId != null) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .border(1.5.dp, Color(0xFFDC2626), RectangleShape)
                            .padding(16.dp)
                            .testTag("active_emergency_card")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTIVE EMERGENCY PING",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                color = Color(0xFFDC2626)
                            )

                            TextButton(
                                onClick = {
                                    activeEmergencyId?.let { onCancelEmergency(it) }
                                    activeEmergencyId = null
                                    emergencyStatus = "Emergency alert resolved"
                                }
                            ) {
                                Text("RESOLVE", color = Color(0xFF06B6D4), fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = emergencyStatus ?: "Emergency trajectory active",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFF1F2937), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("LAST STATION", style = MaterialTheme.typography.labelSmall, color = Color(0xFF6B7280))
                                Text(locationState.nearestStation.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            }
                            Column {
                                Text("NEXT INTERCEPT", style = MaterialTheme.typography.labelSmall, color = Color(0xFF6B7280))
                                Text(locationState.nextStationPredicted?.name ?: "Detecting", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF22C55E))
                            }
                            Column {
                                Text("SPEED", style = MaterialTheme.typography.labelSmall, color = Color(0xFF6B7280))
                                Text("${locationState.speedKmh} km/h", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            }
                        }
                    }
                }
            }

            // SLIDE TO SOS (Flat, full width, sharp edges)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .border(1.dp, Color(0xFF1F2937), RectangleShape)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "SWIPE TO INITIATE EMERGENCY SOS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FlatSlideToSosButton(
                        enabled = !isCountingDown,
                        onSlideCompleted = {
                            isCountingDown = true
                        }
                    )
                }
            }

            // Emergency Trajectory Context (Flat border-b border-gray-800)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .border(1.dp, Color(0xFF1F2937), RectangleShape)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = null,
                            tint = Color(0xFF06B6D4),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LIVE POLICE INTERCEPT VECTOR",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF06B6D4)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Notifies railway police at upcoming station for immediate platform intercept.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF1F2937), RectangleShape)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("GPS COORDS", style = MaterialTheme.typography.labelSmall, color = Color(0xFF6B7280))
                            Text(
                                "${String.format("%.4f", locationState.latitude)}, ${String.format("%.4f", locationState.longitude)}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("ACCURACY", style = MaterialTheme.typography.labelSmall, color = Color(0xFF6B7280))
                            Text(
                                "±${locationState.accuracyMeters.roundToInt()}m",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF22C55E)
                            )
                        }
                    }
                }
            }

            // Helplines Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .border(1.dp, Color(0xFF1F2937), RectangleShape)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "MUMBAI SUBURBAN HELPLINES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )
                }
            }

            item {
                FlatHelplineRow(
                    title = "National Emergency Number",
                    number = "112",
                    description = "Police, Fire, Ambulance",
                    color = Color(0xFFDC2626),
                    onDial = { dialNumber(context, "112") }
                )
            }

            item {
                FlatHelplineRow(
                    title = "GRP Railway Police Helpline",
                    number = "1512",
                    description = "Mumbai local train security",
                    color = Color(0xFF06B6D4),
                    onDial = { dialNumber(context, "1512") }
                )
            }

            item {
                FlatHelplineRow(
                    title = "RPF Security Helpline",
                    number = "139",
                    description = "Indian Railways security",
                    color = Color(0xFFEAB308),
                    onDial = { dialNumber(context, "139") }
                )
            }

            item {
                FlatHelplineRow(
                    title = "Women's Safety Helpline",
                    number = "9833312222",
                    description = "Suburban rail women safety cell",
                    color = Color(0xFF22C55E),
                    onDial = { dialNumber(context, "9833312222") }
                )
            }
        }
    }
}

@Composable
fun FlatSlideToSosButton(
    enabled: Boolean,
    onSlideCompleted: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val maxDragDistance = 240f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFF7F1D1D)) // red-950
            .border(2.dp, Color(0xFFDC2626), RectangleShape)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "SLIDE TO TRIGGER SOS  >>>",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp
            ),
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .size(54.dp)
                .background(Color(0xFFDC2626)) // red-600
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX >= maxDragDistance) {
                                onSlideCompleted()
                            }
                            offsetX = 0f
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = (offsetX + dragAmount).coerceIn(0f, maxDragDistance + 20f)
                            offsetX = newOffset
                        }
                    )
                }
                .testTag("slide_to_sos_knob"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "SOS Knob",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun FlatHelplineRow(
    title: String,
    number: String,
    description: String,
    color: Color,
    onDial: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .border(1.dp, Color(0xFF1F2937), RectangleShape)
            .clickable { onDial() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9CA3AF)
            )
        }

        Box(
            modifier = Modifier
                .background(color)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                color = Color.White
            )
        }
    }
}

private fun triggerEmergencyNow(
    context: Context,
    locationState: TransitLocationState,
    onTriggerEmergency: (EmergencyAlert) -> Unit
) {
    dialNumber(context, "112")
    val alert = EmergencyAlert(
        latitude = locationState.latitude,
        longitude = locationState.longitude,
        nearestStation = locationState.nearestStation.name,
        nextStationPredicted = locationState.nextStationPredicted?.name ?: "Next Station",
        heading = locationState.heading,
        speedKmh = locationState.speedKmh,
        emergencyType = "SOS_112",
        status = "ACTIVE",
        resolved = false
    )
    onTriggerEmergency(alert)
}

private fun dialNumber(context: Context, number: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback
    }
}

private fun vibrateDevice(context: Context, millis: Long) {
    try {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        @Suppress("DEPRECATION")
        v?.vibrate(millis)
    } catch (e: Exception) {
        // Ignore
    }
}
