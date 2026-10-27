package com.example.data.model

data class Station(
    val id: Int,
    val name: String,
    val code: String,
    val line: String, // "Western", "Central", "Harbour", "Trans-Harbour", "Uran", "Diva-Roha"
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double = 0.0,
    val timeMinutes: Int = 0,
    val platforms: Int = 2,
    val nearbyAttractions: String = ""
)

enum class TrainDirection {
    UP, // Towards South / Terminus (Churchgate / CSMT)
    DOWN, // Towards North / Suburbs (Virar / Kasara / Panvel)
    STATIONARY,
    UNKNOWN
}

enum class CrowdDensity(val label: String, val description: String, val colorHex: Long) {
    GREEN_SEATS("Not Crowded", "Seats available, light crowd", 0xFF00E676),
    YELLOW_STAND("Moderately Crowded", "Standing space available, moderate rush", 0xFFFFD600),
    RED_CRUSH("Packed", "Super dense crush load, heavily packed", 0xFFFF1744)
}

enum class CrowdCategory {
    PLATFORM,
    TRAIN
}
