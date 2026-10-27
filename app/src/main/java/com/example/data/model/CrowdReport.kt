package com.example.data.model

import com.google.firebase.Timestamp

data class CrowdReport(
    val reportId: String = "",
    val userId: String = "",
    val userName: String = "Commuter",
    val timestamp: Timestamp? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val nearestStation: String = "",
    val line: String = "Western",
    val speedKmh: Double = 0.0,
    val direction: String = "UNKNOWN", // UP, DOWN, STATIONARY, UNKNOWN
    val crowdLevel: String = "YELLOW_STAND", // GREEN_SEATS, YELLOW_STAND, RED_CRUSH
    val crowdCategory: String = "TRAIN", // PLATFORM, TRAIN
    val isSynced: Boolean = true
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "reportId" to reportId,
        "userId" to userId,
        "userName" to userName,
        "latitude" to latitude,
        "longitude" to longitude,
        "nearestStation" to nearestStation,
        "line" to line,
        "speedKmh" to speedKmh,
        "direction" to direction,
        "crowdLevel" to crowdLevel,
        "crowdCategory" to crowdCategory,
        "isSynced" to isSynced
    )
}

data class EmergencyAlert(
    val emergencyId: String = "",
    val userId: String = "",
    val userName: String = "Commuter",
    val timestamp: Timestamp? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val nearestStation: String = "",
    val nextStationPredicted: String = "",
    val heading: Double = 0.0,
    val speedKmh: Double = 0.0,
    val emergencyType: String = "SOS",
    val status: String = "ACTIVE", // ACTIVE, CANCELLED, RESOLVED
    val resolved: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "emergencyId" to emergencyId,
        "userId" to userId,
        "userName" to userName,
        "latitude" to latitude,
        "longitude" to longitude,
        "nearestStation" to nearestStation,
        "nextStationPredicted" to nextStationPredicted,
        "heading" to heading,
        "speedKmh" to speedKmh,
        "emergencyType" to emergencyType,
        "status" to status,
        "resolved" to resolved
    )
}

data class StationPrediction(
    val stationName: String,
    val line: String,
    val predictedDensity: String, // GREEN_SEATS, YELLOW_STAND, RED_CRUSH
    val confidence: String,
    val advice: String
)
