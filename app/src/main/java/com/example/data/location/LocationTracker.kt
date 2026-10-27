package com.example.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.example.data.model.CrowdCategory
import com.example.data.model.Station
import com.example.data.model.TrainDirection
import com.example.data.repository.StationRepository
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

data class TransitLocationState(
    val latitude: Double = 18.9697, // Defaults to Mumbai Central area if no GPS yet
    val longitude: Double = 72.8194,
    val speedKmh: Double = 0.0,
    val heading: Double = 0.0,
    val accuracyMeters: Float = 0f,
    val nearestStation: Station = StationRepository.stations[4], // Mumbai Central
    val nextStationPredicted: Station? = StationRepository.stations[5], // Mahalaxmi
    val direction: TrainDirection = TrainDirection.STATIONARY,
    val crowdCategory: CrowdCategory = CrowdCategory.PLATFORM,
    val hasPermission: Boolean = false,
    val isTracking: Boolean = false,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)

class LocationTracker(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _locationState = MutableStateFlow(TransitLocationState())
    val locationState: StateFlow<TransitLocationState> = _locationState.asStateFlow()

    private var lastLocation: Location? = null
    private var lastTimestamp: Long = 0L

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            processNewLocation(location)
        }
    }

    fun updatePermissionStatus(granted: Boolean) {
        _locationState.value = _locationState.value.copy(hasPermission = granted)
        if (granted && !_locationState.value.isTracking) {
            startTracking()
        }
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        try {
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
                .setMinUpdateIntervalMillis(1500L)
                .setMinUpdateDistanceMeters(2.0f)
                .build()

            fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
            _locationState.value = _locationState.value.copy(isTracking = true)

            // Also request last known location immediately
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    processNewLocation(loc)
                }
            }
        } catch (e: SecurityException) {
            Log.w("LocationTracker", "Location permission missing: ${e.message}")
            _locationState.value = _locationState.value.copy(isTracking = false, hasPermission = false)
        }
    }

    fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        _locationState.value = _locationState.value.copy(isTracking = false)
    }

    private fun processNewLocation(location: Location) {
        val now = System.currentTimeMillis()
        val prev = lastLocation
        val prevTime = lastTimestamp

        // 1. Calculate Speed in km/h
        var calculatedSpeedKmh = if (location.hasSpeed() && location.speed > 0f) {
            location.speed * 3.6
        } else {
            0.0
        }

        // SPEED FALLBACK: If device speed is 0 or null, calculate via Haversine velocity between two recorded coordinates
        if (calculatedSpeedKmh <= 0.0 && prev != null && prevTime > 0L) {
            val deltaSeconds = (now - prevTime) / 1000.0
            if (deltaSeconds in 0.5..60.0) {
                val distanceKm = StationRepository.calculateHaversineDistanceKm(
                    prev.latitude, prev.longitude,
                    location.latitude, location.longitude
                )
                val hours = deltaSeconds / 3600.0
                val fallbackSpeed = distanceKm / hours
                if (fallbackSpeed in 0.5..160.0) {
                    calculatedSpeedKmh = fallbackSpeed
                }
            }
        }

        // 2. Tag crowd category based on speed threshold (< 10 km/h: Platform Crowding, > 10 km/h: Train Crowding)
        val category = if (calculatedSpeedKmh < 10.0) {
            CrowdCategory.PLATFORM
        } else {
            CrowdCategory.TRAIN
        }

        // 3. Direction calculation (UP vs DOWN based on coordinate progression)
        val dir = if (prev != null) {
            StationRepository.detectDirection(prev.latitude, location.latitude, calculatedSpeedKmh)
        } else {
            TrainDirection.STATIONARY
        }

        // 4. Find nearest station from 141 stations database
        val nearest = StationRepository.findNearestStation(location.latitude, location.longitude)

        // 5. Predict next station
        val upcoming = StationRepository.getNextUpcomingStations(nearest, dir, 2)
        val nextPredicted = upcoming.firstOrNull { it.id != nearest.id } ?: upcoming.firstOrNull()

        // 6. Heading
        val headingDeg = if (location.hasBearing()) location.bearing.toDouble() else 0.0

        // Round speed to 1 decimal place
        val formattedSpeed = (calculatedSpeedKmh * 10.0).roundToInt() / 10.0

        _locationState.value = TransitLocationState(
            latitude = location.latitude,
            longitude = location.longitude,
            speedKmh = formattedSpeed,
            heading = headingDeg,
            accuracyMeters = location.accuracy,
            nearestStation = nearest,
            nextStationPredicted = nextPredicted,
            direction = dir,
            crowdCategory = category,
            hasPermission = true,
            isTracking = true,
            lastUpdatedMillis = now
        )

        lastLocation = location
        lastTimestamp = now
    }

    // Helper for manual demo simulation or test verification
    fun simulateLocation(station: Station, speed: Double, direction: TrainDirection) {
        val next = StationRepository.getNextUpcomingStations(station, direction, 1).firstOrNull()
        _locationState.value = _locationState.value.copy(
            latitude = station.latitude,
            longitude = station.longitude,
            speedKmh = speed,
            nearestStation = station,
            nextStationPredicted = next,
            direction = direction,
            crowdCategory = if (speed < 10.0) CrowdCategory.PLATFORM else CrowdCategory.TRAIN,
            lastUpdatedMillis = System.currentTimeMillis()
        )
    }
}
