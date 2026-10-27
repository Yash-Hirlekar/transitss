package com.example.ui.timetable

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.location.LocationTracker
import com.example.data.model.Station
import com.example.data.model.TrainDirection
import com.example.data.model.TrainSchedule
import com.example.data.repository.StationRepository
import com.example.data.repository.TimetableRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TimetableUiState(
    val selectedLine: String = "Western",
    val selectedDirection: TrainDirection = TrainDirection.DOWN,
    val sourceStation: Station = StationRepository.stations.first { it.line == "Western" }, // Churchgate
    val destinationStation: Station? = null,
    val searchQuery: String = "",
    val filterFast: Boolean = false,
    val filterSlow: Boolean = false,
    val filterAC: Boolean = false,
    val filter15Car: Boolean = false,
    val filterLadiesSpecial: Boolean = false,
    val selectedTrainForDetails: TrainSchedule? = null,
    val trainsList: List<TrainSchedule> = emptyList(),
    val upcomingTrains: List<Pair<TrainSchedule, String>> = emptyList()
)

class TimetableViewModel(application: Application) : AndroidViewModel(application) {

    private val locationTracker = LocationTracker(application.applicationContext)

    private val _uiState = MutableStateFlow(TimetableUiState())
    val uiState: StateFlow<TimetableUiState> = _uiState.asStateFlow()

    init {
        // Auto-select nearest station if matching line
        viewModelScope.launch {
            locationTracker.locationState.collect { loc ->
                val curr = _uiState.value
                if (loc.nearestStation.line == curr.selectedLine) {
                    _uiState.value = curr.copy(
                        sourceStation = loc.nearestStation,
                        selectedDirection = if (loc.direction != TrainDirection.UNKNOWN && loc.direction != TrainDirection.STATIONARY) loc.direction else curr.selectedDirection
                    )
                    updateTrainList()
                }
            }
        }
        updateTrainList()
    }

    fun selectLine(line: String) {
        val defaultStation = StationRepository.stations.firstOrNull { it.line == line }
            ?: StationRepository.stations.first()

        val defaultDir = when (line) {
            "Central" -> TrainDirection.DOWN
            "Harbour" -> TrainDirection.DOWN
            "Trans-Harbour" -> TrainDirection.DOWN
            "Uran" -> TrainDirection.DOWN
            else -> TrainDirection.DOWN
        }

        _uiState.value = _uiState.value.copy(
            selectedLine = line,
            sourceStation = defaultStation,
            destinationStation = null,
            selectedDirection = defaultDir
        )
        updateTrainList()
    }

    fun toggleDirection() {
        val newDir = if (_uiState.value.selectedDirection == TrainDirection.DOWN) TrainDirection.UP else TrainDirection.DOWN
        _uiState.value = _uiState.value.copy(selectedDirection = newDir)
        updateTrainList()
    }

    fun setDirection(direction: TrainDirection) {
        _uiState.value = _uiState.value.copy(selectedDirection = direction)
        updateTrainList()
    }

    fun setSourceStation(station: Station) {
        _uiState.value = _uiState.value.copy(sourceStation = station)
        updateTrainList()
    }

    fun setDestinationStation(station: Station?) {
        _uiState.value = _uiState.value.copy(destinationStation = station)
        updateTrainList()
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        updateTrainList()
    }

    fun toggleFilterFast() {
        _uiState.value = _uiState.value.copy(
            filterFast = !_uiState.value.filterFast,
            filterSlow = if (!_uiState.value.filterFast) false else _uiState.value.filterSlow
        )
        updateTrainList()
    }

    fun toggleFilterSlow() {
        _uiState.value = _uiState.value.copy(
            filterSlow = !_uiState.value.filterSlow,
            filterFast = if (!_uiState.value.filterSlow) false else _uiState.value.filterFast
        )
        updateTrainList()
    }

    fun toggleFilterAC() {
        _uiState.value = _uiState.value.copy(filterAC = !_uiState.value.filterAC)
        updateTrainList()
    }

    fun toggleFilter15Car() {
        _uiState.value = _uiState.value.copy(filter15Car = !_uiState.value.filter15Car)
        updateTrainList()
    }

    fun toggleFilterLadiesSpecial() {
        _uiState.value = _uiState.value.copy(filterLadiesSpecial = !_uiState.value.filterLadiesSpecial)
        updateTrainList()
    }

    fun selectTrainForDetails(train: TrainSchedule?) {
        _uiState.value = _uiState.value.copy(selectedTrainForDetails = train)
    }

    private fun updateTrainList() {
        val curr = _uiState.value
        val allFiltered = TimetableRepository.getFilteredSchedules(
            line = curr.selectedLine,
            direction = curr.selectedDirection,
            sourceStation = curr.sourceStation.name,
            destinationStation = curr.destinationStation?.name,
            fastOnly = curr.filterFast,
            slowOnly = curr.filterSlow,
            acOnly = curr.filterAC,
            car15Only = curr.filter15Car,
            ladiesOnly = curr.filterLadiesSpecial
        )

        val searched = if (curr.searchQuery.isBlank()) {
            allFiltered
        } else {
            val q = curr.searchQuery.trim().lowercase()
            allFiltered.filter {
                it.trainNumber.contains(q) ||
                it.trainCode.lowercase().contains(q) ||
                it.destinationStation.lowercase().contains(q) ||
                it.destinationCode.lowercase().contains(q)
            }
        }

        val upcoming = TimetableRepository.getUpcomingTrainsForStation(
            stationName = curr.sourceStation.name,
            line = curr.selectedLine,
            direction = curr.selectedDirection,
            limit = 6
        )

        _uiState.value = _uiState.value.copy(
            trainsList = searched,
            upcomingTrains = upcoming
        )
    }
}
