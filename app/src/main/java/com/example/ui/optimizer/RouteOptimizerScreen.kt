package com.example.ui.optimizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.location.TransitLocationState
import com.example.data.model.Station
import com.example.data.model.TrainDirection
import com.example.data.model.TrainSpeedType
import com.example.data.repository.StationRepository
import com.example.data.repository.TimetableRepository
import com.example.ui.dashboard.FlatMetric
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

data class OptimizedTripResult(
    val origin: Station,
    val destination: Station,
    val targetArrivalTime: String,
    val recommendedDepartureTime: String,
    val durationMinutes: Int,
    val delayBufferMinutes: Int,
    val interchanges: List<String>,
    val predictedCrowdRisk: String,
    val pathStations: List<Station>,
    val tips: String
)

@Composable
fun RouteOptimizerScreen(
    locationState: TransitLocationState,
    modifier: Modifier = Modifier
) {
    var originStation by remember { mutableStateOf(locationState.nearestStation) }
    var destinationStation by remember { mutableStateOf(StationRepository.minimalStations.firstOrNull { it.name.contains("Dadar") } ?: StationRepository.minimalStations[2]) }

    var selectedTargetTimeStr by remember { mutableStateOf("10:30 AM") }
    var showOriginDialog by remember { mutableStateOf(false) }
    var showDestDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var calculatedResult by remember { mutableStateOf<OptimizedTripResult?>(null) }

    LaunchedEffect(originStation, destinationStation) {
        calculatedResult = calculateOptimizedTrip(originStation, destinationStation, selectedTargetTimeStr)
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
            // Screen Header (Flat border-b border-gray-800)
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
                        imageVector = Icons.AutoMirrored.Filled.AltRoute,
                        contentDescription = null,
                        tint = Color(0xFFEAB308),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ROUTE OPTIMIZER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Reach-By predictive routing & buffer planner",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }

            // Input Form Section (Flat pure black with border-b border-gray-800)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .border(1.dp, Color(0xFF1F2937), RectangleShape)
                        .padding(16.dp)
                ) {
                    // Origin Station
                    Text(
                        text = "ORIGIN STATION (CURRENT GEOFENCE)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    StationPickerButton(
                        station = originStation,
                        onClick = {
                            searchQuery = ""
                            showOriginDialog = true
                        },
                        testTag = "origin_station_picker"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Destination Station
                    Text(
                        text = "DESTINATION STATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    StationPickerButton(
                        station = destinationStation,
                        onClick = {
                            searchQuery = ""
                            showDestDialog = true
                        },
                        testTag = "dest_station_picker"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target Arrival Time Selector (Flat buttons)
                    Text(
                        text = "TARGET ARRIVAL TIME",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF1F2937), RectangleShape)
                    ) {
                        listOf("09:00 AM", "10:30 AM", "01:30 PM", "06:30 PM", "08:45 PM").forEach { timeOption ->
                            val isSelected = selectedTargetTimeStr == timeOption
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSelected) Color(0xFFEAB308) else Color.Black)
                                    .border(1.dp, Color(0xFF1F2937), RectangleShape)
                                    .clickable {
                                        selectedTargetTimeStr = timeOption
                                        calculatedResult = calculateOptimizedTrip(originStation, destinationStation, timeOption)
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = timeOption.replace(" ", "\n"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.Black else Color(0xFF9CA3AF),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Compute Button (Flat full-width)
                    Button(
                        onClick = {
                            calculatedResult = calculateOptimizedTrip(originStation, destinationStation, selectedTargetTimeStr)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("calculate_route_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEAB308), // yellow-500
                            contentColor = Color.Black
                        ),
                        shape = RectangleShape
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "COMPUTE PROBABILISTIC ROUTE",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
                        )
                    }
                }
            }

            // Calculation Results
            calculatedResult?.let { res ->
                item {
                    TripRecommendationSection(result = res)
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .border(1.dp, Color(0xFF1F2937), RectangleShape)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "ROUTE STATIONS BREAKDOWN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White
                        )
                    }
                }

                items(res.pathStations) { st ->
                    StationStepRow(
                        station = st,
                        isOrigin = st.id == res.origin.id,
                        isDestination = st.id == res.destination.id,
                        isInterchange = res.interchanges.contains(st.name)
                    )
                }
            }
        }
    }

    // Origin Station Dialog
    if (showOriginDialog) {
        StationSelectionDialog(
            title = "Select Origin Station",
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onStationSelected = {
                originStation = it
                showOriginDialog = false
            },
            onDismiss = { showOriginDialog = false }
        )
    }

    // Destination Station Dialog
    if (showDestDialog) {
        StationSelectionDialog(
            title = "Select Destination Station",
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onStationSelected = {
                destinationStation = it
                showDestDialog = false
            },
            onDismiss = { showDestDialog = false }
        )
    }
}

@Composable
fun StationPickerButton(
    station: Station,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .border(1.dp, Color(0xFF1F2937), RectangleShape)
            .clickable { onClick() }
            .testTag(testTag)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "${station.line} Line • Code: ${station.code}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9CA3AF)
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select",
                tint = Color(0xFF06B6D4)
            )
        }
    }
}

@Composable
fun TripRecommendationSection(result: OptimizedTripResult) {
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
            Text(
                text = "DEPARTURE RECOMMENDATION",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFEAB308)
            )

            Box(
                modifier = Modifier
                    .background(Color(0xFF713F12))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Buffer: +${result.delayBufferMinutes}m",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("LEAVE BY", style = MaterialTheme.typography.labelSmall, color = Color(0xFF6B7280))
                Text(
                    text = result.recommendedDepartureTime,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = Color(0xFF22C55E) // green
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("TARGET ARRIVAL", style = MaterialTheme.typography.labelSmall, color = Color(0xFF6B7280))
                Text(
                    text = result.targetArrivalTime,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFF1F2937), thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FlatMetric(label = "TRANSIT TIME", value = "${result.durationMinutes} mins")
            FlatMetric(label = "INTERCHANGES", value = if (result.interchanges.isEmpty()) "Direct Train" else result.interchanges.joinToString())
            FlatMetric(label = "PEAK RISK", value = result.predictedCrowdRisk)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Upcoming Scheduled Train Runs (Official Timetable)
        Text(
            text = "UPCOMING SCHEDULED TRAIN RUNS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = Color(0xFFEAB308)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val dir = if (result.origin.id <= result.destination.id) TrainDirection.DOWN else TrainDirection.UP
        val realTrains = remember(result.origin, result.destination) {
            val list = TimetableRepository.getFilteredSchedules(
                line = result.origin.line,
                direction = dir,
                sourceStation = result.origin.name,
                destinationStation = result.destination.name
            )
            if (list.isNotEmpty()) list.take(3) else {
                TimetableRepository.allSchedules.filter { it.line == result.origin.line && it.direction == dir }.take(3)
            }
        }

        if (realTrains.isEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                FlatTrainRow(
                    time = result.recommendedDepartureTime,
                    serviceType = "Fast Local (12-Car)",
                    platform = "PF 3",
                    crowdTag = "Seats Available",
                    crowdColor = Color(0xFF16A34A)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                realTrains.forEach { train ->
                    val stop = train.stoppages.firstOrNull { it.stationName.contains(result.origin.name, ignoreCase = true) }
                    val depTime = stop?.time ?: train.departureTime
                    val typeDesc = "${if (train.isAC) "AC EMU" else if (train.speedType == TrainSpeedType.FAST) "Fast Local" else "Slow Local"} • ${if (train.is15Car) "15-Car" else "12-Car"} (${train.destinationCode})"
                    val crowdColor = when {
                        train.isAC -> Color(0xFF0284C7)
                        train.speedType == TrainSpeedType.FAST -> Color(0xFFCA8A04)
                        else -> Color(0xFF16A34A)
                    }
                    val crowdTag = when {
                        train.isAC -> "AC ❄️"
                        train.speedType == TrainSpeedType.FAST -> "Fast Rush"
                        else -> "Seats Avail"
                    }
                    FlatTrainRow(
                        time = depTime,
                        serviceType = typeDesc,
                        platform = train.platform,
                        crowdTag = crowdTag,
                        crowdColor = crowdColor
                    )
                }
            }
        }
    }
}

@Composable
fun FlatTrainRow(
    time: String,
    serviceType: String,
    platform: String,
    crowdTag: String,
    crowdColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .border(1.dp, Color(0xFF1F2937), RectangleShape)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$time • $serviceType",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = "Departing $platform",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9CA3AF)
            )
        }

        Box(
            modifier = Modifier
                .background(crowdColor)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = crowdTag,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
            )
        }
    }
}

@Composable
fun StationStepRow(
    station: Station,
    isOrigin: Boolean,
    isDestination: Boolean,
    isInterchange: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .border(1.dp, Color(0xFF1F2937), RectangleShape)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = station.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isOrigin || isDestination || isInterchange) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isOrigin || isDestination) Color.White else Color(0xFF9CA3AF)
            )
            if (isInterchange) {
                Text(
                    text = "Interchange Junction (FOB transfer)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF06B6D4)
                )
            }
        }

        Box(
            modifier = Modifier
                .background(Color(0xFF1F2937))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "${station.line} L",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
fun StationSelectionDialog(
    title: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onStationSelected: (Station) -> Unit,
    onDismiss: () -> Unit
) {
    val filtered = remember(searchQuery) {
        StationRepository.searchStations(searchQuery)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.White)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search by station or line...", color = Color(0xFF6B7280)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF06B6D4),
                        unfocusedBorderColor = Color(0xFF1F2937)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filtered) { station ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onStationSelected(station) }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = station.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "${station.line} Line • Code: ${station.code}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF9CA3AF)
                                )
                            }
                        }
                        HorizontalDivider(color = Color(0xFF1F2937))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF06B6D4))
            }
        },
        containerColor = Color.Black,
        shape = RectangleShape
    )
}

fun calculateOptimizedTrip(
    origin: Station,
    destination: Station,
    targetTimeStr: String
): OptimizedTripResult {
    val sameLine = origin.line == destination.line
    val allStations = StationRepository.stations

    val interchanges = mutableListOf<String>()
    val pathStations = mutableListOf<Station>()

    var estimatedDuration = 25
    val delayBuffer = 12

    if (sameLine) {
        val lineList = allStations.filter { it.line == origin.line }
        val originIdx = lineList.indexOfFirst { it.name == origin.name }
        val destIdx = lineList.indexOfFirst { it.name == destination.name }
        if (originIdx != -1 && destIdx != -1) {
            val range = if (originIdx <= destIdx) originIdx..destIdx else originIdx downTo destIdx
            range.forEach { pathStations.add(lineList[it]) }
            estimatedDuration = (pathStations.size * 2.8).roundToInt()
        } else {
            pathStations.add(origin)
            pathStations.add(destination)
        }
    } else {
        val interchangeStationName = when {
            (origin.line == "Western" && destination.line == "Central") ||
            (origin.line == "Central" && destination.line == "Western") -> "Dadar Central"
            (origin.line == "Central" && destination.line == "Harbour") ||
            (origin.line == "Harbour" && destination.line == "Central") -> "Kurla"
            else -> "Thane"
        }
        interchanges.add(interchangeStationName)

        val leg1 = allStations.filter { it.line == origin.line }
        val oIdx = leg1.indexOfFirst { it.name == origin.name }
        if (oIdx != -1) {
            pathStations.addAll(leg1.take(5))
        }
        val leg2 = allStations.filter { it.line == destination.line }
        val dIdx = leg2.indexOfFirst { it.name == destination.name }
        if (dIdx != -1) {
            pathStations.addAll(leg2.takeLast(4))
        }
        estimatedDuration = (pathStations.size * 3.2).roundToInt() + 8
    }

    val totalLeadMinutes = estimatedDuration + delayBuffer
    val depTime = calculateDepartureTime(targetTimeStr, totalLeadMinutes)

    val crowdRisk = when {
        estimatedDuration > 45 -> "HIGH (Peak Stretch)"
        interchanges.isNotEmpty() -> "MODERATE (Transfer Rush)"
        else -> "LOW / MODERATE"
    }

    val tips = if (interchanges.isNotEmpty()) {
        "Transfer at ${interchanges.first()} via Central FOB. Allow 8 mins walking margin during peak hours."
    } else {
        "Fast local saves ~12 mins between Bandra and Borivali / Kurla and Thane."
    }

    return OptimizedTripResult(
        origin = origin,
        destination = destination,
        targetArrivalTime = targetTimeStr,
        recommendedDepartureTime = depTime,
        durationMinutes = estimatedDuration,
        delayBufferMinutes = delayBuffer,
        interchanges = interchanges,
        predictedCrowdRisk = crowdRisk,
        pathStations = pathStations.distinctBy { it.name },
        tips = tips
    )
}

fun calculateDepartureTime(targetTimeStr: String, minutesToSubtract: Int): String {
    return try {
        val sdf = SimpleDateFormat("hh:mm a", Locale.US)
        val date = sdf.parse(targetTimeStr) ?: Date()
        val cal = Calendar.getInstance().apply {
            time = date
            add(Calendar.MINUTE, -minutesToSubtract)
        }
        sdf.format(cal.time)
    } catch (e: Exception) {
        "09:45 AM"
    }
}
