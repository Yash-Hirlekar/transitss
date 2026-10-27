package com.example.ui.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Station
import com.example.data.model.TrainDirection
import com.example.data.model.TrainSchedule
import com.example.data.model.TrainSpeedType
import com.example.data.repository.StationRepository
import com.example.data.repository.TimetableRepository
import com.example.ui.optimizer.StationSelectionDialog

@Composable
fun TimetableScreen(
    viewModel: TimetableViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var showSourceStationDialog by remember { mutableStateOf(false) }
    var showDestStationDialog by remember { mutableStateOf(false) }
    var searchQueryDialog by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .border(1.dp, Color(0xFF1F2937), RectangleShape)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF06B6D4), // Cyan
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "M-INDICATOR TIMETABLES",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Official Mumbai Suburban Railway Schedules",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            // 1. Line Selection Tabs (Western, Central, Harbour, Trans-Harbour, Uran)
            ScrollableTabRow(
                selectedTabIndex = TimetableRepository.lines.indexOf(uiState.selectedLine).coerceAtLeast(0),
                containerColor = Color.Black,
                contentColor = Color(0xFF06B6D4),
                edgePadding = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1F2937), RectangleShape)
            ) {
                TimetableRepository.lines.forEach { lineName ->
                    val isSelected = uiState.selectedLine.equals(lineName, ignoreCase = true)
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.selectLine(lineName) },
                        text = {
                            Text(
                                text = lineName.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) Color(0xFF06B6D4) else Color(0xFF9CA3AF)
                            )
                        },
                        modifier = Modifier.testTag("tab_line_${lineName.lowercase()}")
                    )
                }
            }

            // 2. Direction Switcher (DOWN to Suburbs vs UP to Terminus)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .border(1.dp, Color(0xFF1F2937), RectangleShape)
            ) {
                val isDown = uiState.selectedDirection == TrainDirection.DOWN
                val downLabel = when (uiState.selectedLine) {
                    "Western" -> "DOWN • To Virar / Dahanu"
                    "Central" -> "DOWN • To Kalyan / Kasara"
                    "Harbour" -> "DOWN • To Panvel / Goregaon"
                    "Trans-Harbour" -> "DOWN • To Panvel / Vashi"
                    else -> "DOWN • To Uran"
                }
                val upLabel = when (uiState.selectedLine) {
                    "Western" -> "UP • To Churchgate"
                    "Central" -> "UP • To CSMT"
                    "Harbour" -> "UP • To CSMT"
                    "Trans-Harbour" -> "UP • To Thane"
                    else -> "UP • To Nerul / Belapur"
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (isDown) Color(0xFF164E63) else Color.Black)
                        .clickable { viewModel.setDirection(TrainDirection.DOWN) }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = downLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isDown) FontWeight.Black else FontWeight.Normal,
                            fontSize = 11.sp
                        ),
                        color = if (isDown) Color(0xFF22D3EE) else Color(0xFF9CA3AF),
                        textAlign = TextAlign.Center
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (!isDown) Color(0xFF164E63) else Color.Black)
                        .border(1.dp, Color(0xFF1F2937), RectangleShape)
                        .clickable { viewModel.setDirection(TrainDirection.UP) }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = upLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (!isDown) FontWeight.Black else FontWeight.Normal,
                            fontSize = 11.sp
                        ),
                        color = if (!isDown) Color(0xFF22D3EE) else Color(0xFF9CA3AF),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 3. Station Selection Bar (From Station -> To Station)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .border(1.dp, Color(0xFF1F2937), RectangleShape)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Source station
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            searchQueryDialog = ""
                            showSourceStationDialog = true
                        }
                        .border(1.dp, Color(0xFF1F2937), RectangleShape)
                        .padding(8.dp)
                        .testTag("source_station_btn")
                ) {
                    Text("FROM STATION", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = Color(0xFF6B7280))
                    Text(
                        text = uiState.sourceStation.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = {
                        val prevSrc = uiState.sourceStation
                        val prevDest = uiState.destinationStation
                        if (prevDest != null) {
                            viewModel.setSourceStation(prevDest)
                            viewModel.setDestinationStation(prevSrc)
                            viewModel.toggleDirection()
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap Stations",
                        tint = Color(0xFF06B6D4)
                    )
                }

                // Destination station
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            searchQueryDialog = ""
                            showDestStationDialog = true
                        }
                        .border(1.dp, Color(0xFF1F2937), RectangleShape)
                        .padding(8.dp)
                        .testTag("dest_station_btn")
                ) {
                    Text("TO (OPTIONAL)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = Color(0xFF6B7280))
                    Text(
                        text = uiState.destinationStation?.name ?: "All Trains",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (uiState.destinationStation != null) Color.White else Color(0xFF9CA3AF)
                    )
                }
            }

            // 4. M-Indicator Style Filter Chips (Fast, Slow, AC, 15-Car, Ladies)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .border(1.dp, Color(0xFF1F2937), RectangleShape)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    MIndicatorFilterChip(
                        label = "Fast (F)",
                        isSelected = uiState.filterFast,
                        onClick = { viewModel.toggleFilterFast() },
                        selectedBg = Color(0xFFCA8A04) // Yellow
                    )
                }
                item {
                    MIndicatorFilterChip(
                        label = "Slow (S)",
                        isSelected = uiState.filterSlow,
                        onClick = { viewModel.toggleFilterSlow() },
                        selectedBg = Color(0xFF16A34A) // Green
                    )
                }
                item {
                    MIndicatorFilterChip(
                        label = "AC Local ❄️",
                        isSelected = uiState.filterAC,
                        onClick = { viewModel.toggleFilterAC() },
                        selectedBg = Color(0xFF0284C7) // Blue
                    )
                }
                item {
                    MIndicatorFilterChip(
                        label = "15-Car",
                        isSelected = uiState.filter15Car,
                        onClick = { viewModel.toggleFilter15Car() },
                        selectedBg = Color(0xFF9333EA) // Purple
                    )
                }
                item {
                    MIndicatorFilterChip(
                        label = "Ladies Spl",
                        isSelected = uiState.filterLadiesSpecial,
                        onClick = { viewModel.toggleFilterLadiesSpecial() },
                        selectedBg = Color(0xFFDB2777) // Pink
                    )
                }
            }

            // 5. Schedules List
            val schedules = uiState.trainsList
            if (schedules.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No trains found matching current filter.\nTry adjusting the station or filters.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9CA3AF),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp)
                ) {
                    // Results count header
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black)
                                .border(1.dp, Color(0xFF1F2937), RectangleShape)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${schedules.size} SCHEDULED TRAINS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                                color = Color.White
                            )
                            Text(
                                text = "Tap train for all halts",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    items(schedules) { train ->
                        MIndicatorTrainRow(
                            train = train,
                            sourceStation = uiState.sourceStation.name,
                            onClick = { viewModel.selectTrainForDetails(train) }
                        )
                    }
                }
            }
        }

        // Modal Sheet for Train Details & Stoppage Times
        uiState.selectedTrainForDetails?.let { selectedTrain ->
            TrainStoppagesDialog(
                train = selectedTrain,
                onDismiss = { viewModel.selectTrainForDetails(null) }
            )
        }
    }

    // Source Station Dialog
    if (showSourceStationDialog) {
        val lineStations = StationRepository.stations.filter { it.line == uiState.selectedLine }
        StationSelectionDialog(
            title = "Select Origin Station",
            searchQuery = searchQueryDialog,
            onSearchQueryChange = { searchQueryDialog = it },
            onStationSelected = {
                viewModel.setSourceStation(it)
                showSourceStationDialog = false
            },
            onDismiss = { showSourceStationDialog = false }
        )
    }

    // Destination Station Dialog
    if (showDestStationDialog) {
        StationSelectionDialog(
            title = "Select Destination Station",
            searchQuery = searchQueryDialog,
            onSearchQueryChange = { searchQueryDialog = it },
            onStationSelected = {
                viewModel.setDestinationStation(it)
                showDestStationDialog = false
            },
            onDismiss = { showDestStationDialog = false }
        )
    }
}

@Composable
fun MIndicatorFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedBg: Color
) {
    Box(
        modifier = Modifier
            .background(if (isSelected) selectedBg else Color.Black)
            .border(1.dp, if (isSelected) selectedBg else Color(0xFF374151), RectangleShape)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = if (isSelected) Color.White else Color(0xFF9CA3AF)
        )
    }
}

@Composable
fun MIndicatorTrainRow(
    train: TrainSchedule,
    sourceStation: String,
    onClick: () -> Unit
) {
    // Find departure time from sourceStation
    val stop = train.stoppages.firstOrNull { it.stationName.contains(sourceStation, ignoreCase = true) }
    val displayDepTime = stop?.time ?: train.departureTime
    val countdown = TimetableRepository.formatMinutesUntil(displayDepTime)

    // Destination Badge Color
    val destBadgeColor = when {
        train.destinationCode in listOf("VR", "DRD", "S", "KP") -> Color(0xFFDC2626) // Red (Long distance suburban)
        train.destinationCode in listOf("BVI", "K", "PL") -> Color(0xFFCA8A04) // Yellow / Amber
        train.destinationCode in listOf("CCG", "CSMT") -> Color(0xFF0284C7) // Blue (Terminus)
        else -> Color(0xFF16A34A) // Green
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .border(1.dp, Color(0xFF1F2937), RectangleShape)
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("train_row_${train.trainNumber}"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Column: Time & Countdown
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = displayDepTime,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1F2937))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = countdown,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF22D3EE)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Sub-details: Train No, Code, Cars
            Text(
                text = "${train.trainNumber} • ${train.trainCode} • ${if (train.is15Car) "15-CAR" else "12-CAR"}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9CA3AF)
            )
        }

        // Right Column: Destination Badge & Tag Pills
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Speed pill (Fast / Slow)
                val speedColor = if (train.speedType == TrainSpeedType.FAST) Color(0xFFEAB308) else Color(0xFF06B6D4)
                Box(
                    modifier = Modifier
                        .border(1.dp, speedColor, RectangleShape)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (train.speedType == TrainSpeedType.FAST) "FAST" else "SLOW",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Black),
                        color = speedColor
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Destination Badge
                Box(
                    modifier = Modifier
                        .background(destBadgeColor)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = train.destinationCode,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Feature indicators (AC, 15-Car, Ladies Spl, Platform)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (train.isAC) {
                    Box(modifier = Modifier.background(Color(0xFF0284C7)).padding(horizontal = 5.dp, vertical = 2.dp)) {
                        Text("AC ❄️", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold))
                    }
                }
                if (train.isLadiesSpecial) {
                    Box(modifier = Modifier.background(Color(0xFFDB2777)).padding(horizontal = 5.dp, vertical = 2.dp)) {
                        Text("LADIES", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold))
                    }
                }
                Box(modifier = Modifier.background(Color(0xFF1F2937)).padding(horizontal = 5.dp, vertical = 2.dp)) {
                    Text(train.platform, color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp))
                }
            }
        }
    }
}

@Composable
fun TrainStoppagesDialog(
    train: TrainSchedule,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${train.originStation} ➔ ${train.destinationStation}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF9CA3AF))
                    }
                }
                Text(
                    text = "${train.trainNumber} (${train.trainCode}) • ${train.line} Line • ${if (train.isAC) "AC EMU" else "Normal EMU"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF06B6D4)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                Text(
                    text = "ALL SCHEDULED HALTS & TIMINGS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                    color = Color(0xFFEAB308),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(train.stoppages) { stop ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF1F2937), RectangleShape)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(if (stop.stationName == train.destinationStation) Color(0xFFDC2626) else Color(0xFF06B6D4))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = stop.stationName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = stop.platform,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }

                            Text(
                                text = stop.time,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color(0xFF22D3EE)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("DONE", color = Color(0xFF06B6D4), fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.Black,
        shape = RectangleShape
    )
}
