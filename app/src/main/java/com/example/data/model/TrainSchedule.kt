package com.example.data.model

data class StationStopTime(
    val stationName: String,
    val stationCode: String,
    val time: String, // "08:42" (24-hour format)
    val platform: String = "PF 1"
)

enum class TrainSpeedType(val label: String, val shortTag: String) {
    FAST("Fast", "F"),
    SLOW("Slow", "S"),
    SEMI_FAST("Semi-Fast", "SF")
}

data class TrainSchedule(
    val trainNumber: String, // e.g. "90123"
    val trainCode: String, // e.g. "VR 21", "B 5", "TPL 7", "K 15"
    val line: String, // "Western", "Central", "Harbour", "Trans-Harbour", "Uran"
    val direction: TrainDirection, // UP or DOWN
    val originStation: String,
    val destinationStation: String,
    val destinationCode: String, // e.g. "VR", "BVI", "K", "S", "PL", "BR", "TNA"
    val departureTime: String, // "08:42"
    val arrivalTime: String, // "09:35"
    val speedType: TrainSpeedType = TrainSpeedType.SLOW,
    val isAC: Boolean = false,
    val is15Car: Boolean = false,
    val isLadiesSpecial: Boolean = false,
    val platform: String = "PF 1",
    val runDays: String = "Daily", // "Daily", "Not on Sun", "Mon-Fri", "Sunday Only"
    val stoppages: List<StationStopTime> = emptyList()
)
