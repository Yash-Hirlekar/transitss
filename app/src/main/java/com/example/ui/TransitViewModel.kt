package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.location.LocationTracker
import com.example.data.location.TransitLocationState
import com.example.data.model.CrowdReport
import com.example.data.model.EmergencyAlert
import com.example.data.model.StationPrediction
import com.example.data.repository.CrowdRepository
import com.example.data.repository.EmergencyRepository
import com.example.data.repository.GeminiTransitService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransitViewModel(application: Application) : AndroidViewModel(application) {

    val locationTracker = LocationTracker(application.applicationContext)
    private val crowdRepository = CrowdRepository(application.applicationContext)
    private val emergencyRepository = EmergencyRepository(application.applicationContext)
    private val geminiService = GeminiTransitService()

    val locationState: StateFlow<TransitLocationState> = locationTracker.locationState

    private val _recentReports = MutableStateFlow<List<CrowdReport>>(emptyList())
    val recentReports: StateFlow<List<CrowdReport>> = _recentReports.asStateFlow()

    private val _predictions = MutableStateFlow<List<StationPrediction>>(emptyList())
    val predictions: StateFlow<List<StationPrediction>> = _predictions.asStateFlow()

    private val _isLoadingPredictions = MutableStateFlow(false)
    val isLoadingPredictions: StateFlow<Boolean> = _isLoadingPredictions.asStateFlow()

    private val _isSubmittingReport = MutableStateFlow(false)
    val isSubmittingReport: StateFlow<Boolean> = _isSubmittingReport.asStateFlow()

    private val _pendingOfflineCount = MutableStateFlow(0)
    val pendingOfflineCount: StateFlow<Int> = _pendingOfflineCount.asStateFlow()

    init {
        // Observe live crowd reports from Firestore
        viewModelScope.launch {
            crowdRepository.observeRecentReports(50).collect { reports ->
                _recentReports.value = reports
                // If we don't have predictions yet, refresh them
                if (_predictions.value.isEmpty()) {
                    refreshPredictions()
                }
            }
        }

        // Periodic sync of offline reports
        viewModelScope.launch {
            _pendingOfflineCount.value = crowdRepository.getPendingCount()
            crowdRepository.syncPendingReports()
        }
    }

    fun isOnline(): Boolean = crowdRepository.isOnline()

    fun refreshPredictions() {
        viewModelScope.launch {
            _isLoadingPredictions.value = true
            val loc = locationState.value
            val preds = geminiService.predictUpcomingCrowd(
                currentStation = loc.nearestStation,
                direction = loc.direction,
                recentReports = _recentReports.value
            )
            _predictions.value = preds
            _isLoadingPredictions.value = false
        }
    }

    fun submitCrowdReport(report: CrowdReport) {
        viewModelScope.launch {
            _isSubmittingReport.value = true
            crowdRepository.submitReport(report)
            _pendingOfflineCount.value = crowdRepository.getPendingCount()
            _isSubmittingReport.value = false
            // Re-fetch prediction with updated crowd data
            refreshPredictions()
        }
    }

    fun triggerEmergency(alert: EmergencyAlert, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val result = emergencyRepository.createEmergencyAlert(alert)
            result.onSuccess { id ->
                onCreated(id)
            }
        }
    }

    fun cancelEmergency(emergencyId: String) {
        viewModelScope.launch {
            emergencyRepository.cancelEmergency(emergencyId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationTracker.stopTracking()
    }
}
