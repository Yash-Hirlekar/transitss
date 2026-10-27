package com.example.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.location.LocationTracker
import com.example.data.model.CrowdCategory
import com.example.data.model.CrowdDensity
import com.example.data.model.CrowdReport
import com.example.data.model.Station
import com.example.data.model.TrainDirection
import com.example.data.repository.CrowdRepository
import com.example.data.repository.StationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SubmissionState {
    object Idle : SubmissionState
    object Submitting : SubmissionState
    data class Success(val message: String, val stationName: String, val densityLabel: String) : SubmissionState
    data class Error(val errorMessage: String) : SubmissionState
}

data class CrowdReportingUiState(
    val currentStation: Station = StationRepository.minimalStations[0], // Churchgate default
    val availableStations: List<Station> = StationRepository.minimalStations,
    val selectedDensity: CrowdDensity = CrowdDensity.YELLOW_STAND, // 'Moderately Crowded'
    val crowdCategory: CrowdCategory = CrowdCategory.PLATFORM,
    val trainDirection: TrainDirection = TrainDirection.UP,
    val coachType: String = "General",
    val speedKmh: Double = 0.0,
    val submissionState: SubmissionState = SubmissionState.Idle,
    val isOnline: Boolean = true,
    val pendingOfflineCount: Int = 0
)

class CrowdReportingViewModel(application: Application) : AndroidViewModel(application) {

    private val crowdRepository = CrowdRepository(application.applicationContext)
    val locationTracker = LocationTracker(application.applicationContext)

    private val _uiState = MutableStateFlow(CrowdReportingUiState())
    val uiState: StateFlow<CrowdReportingUiState> = _uiState.asStateFlow()

    private val _recentReports = MutableStateFlow<List<CrowdReport>>(emptyList())
    val recentReports: StateFlow<List<CrowdReport>> = _recentReports.asStateFlow()

    init {
        // Sync with location tracker to auto-set current geofenced station if available
        viewModelScope.launch {
            locationTracker.locationState.collect { loc ->
                _uiState.value = _uiState.value.copy(
                    currentStation = loc.nearestStation,
                    speedKmh = loc.speedKmh,
                    crowdCategory = loc.crowdCategory,
                    trainDirection = loc.direction
                )
            }
        }

        // Observe real-time crowd reports from Firestore
        viewModelScope.launch {
            crowdRepository.observeRecentReports(30).collect { reports ->
                _recentReports.value = reports
            }
        }

        _uiState.value = _uiState.value.copy(
            isOnline = crowdRepository.isOnline(),
            pendingOfflineCount = crowdRepository.getPendingCount()
        )
    }

    fun selectStation(station: Station) {
        _uiState.value = _uiState.value.copy(currentStation = station)
    }

    fun setCrowdDensity(density: CrowdDensity) {
        _uiState.value = _uiState.value.copy(selectedDensity = density)
    }

    fun setCrowdCategory(category: CrowdCategory) {
        _uiState.value = _uiState.value.copy(crowdCategory = category)
    }

    fun setTrainDirection(direction: TrainDirection) {
        _uiState.value = _uiState.value.copy(trainDirection = direction)
    }

    fun setCoachType(coach: String) {
        _uiState.value = _uiState.value.copy(coachType = coach)
    }

    fun submitReport() {
        val current = _uiState.value
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(submissionState = SubmissionState.Submitting)

            val report = CrowdReport(
                latitude = current.currentStation.latitude,
                longitude = current.currentStation.longitude,
                nearestStation = current.currentStation.name,
                line = current.currentStation.line,
                speedKmh = current.speedKmh,
                direction = current.trainDirection.name,
                crowdLevel = current.selectedDensity.name, // "GREEN_SEATS", "YELLOW_STAND", "RED_CRUSH"
                crowdCategory = current.crowdCategory.name, // "PLATFORM", "TRAIN"
                isSynced = crowdRepository.isOnline()
            )

            val result = crowdRepository.submitReport(report)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    submissionState = SubmissionState.Success(
                        message = "Report logged: ${current.selectedDensity.label} at ${current.currentStation.name}",
                        stationName = current.currentStation.name,
                        densityLabel = current.selectedDensity.label
                    ),
                    pendingOfflineCount = crowdRepository.getPendingCount()
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    submissionState = SubmissionState.Error(
                        errorMessage = error.localizedMessage ?: "Failed to log crowd report"
                    )
                )
            }
        }
    }

    fun resetSubmissionState() {
        _uiState.value = _uiState.value.copy(submissionState = SubmissionState.Idle)
    }
}
