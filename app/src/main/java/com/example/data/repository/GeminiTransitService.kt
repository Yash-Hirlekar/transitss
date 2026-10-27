package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.CrowdDensity
import com.example.data.model.CrowdReport
import com.example.data.model.Station
import com.example.data.model.StationPrediction
import com.example.data.model.TrainDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiTransitService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun predictUpcomingCrowd(
        currentStation: Station,
        direction: TrainDirection,
        recentReports: List<CrowdReport>
    ): List<StationPrediction> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val upcomingStations = StationRepository.getNextUpcomingStations(currentStation, direction, 4)

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Provide intelligent algorithmic prediction based on peak hours and recent reports
            return@withContext generateHeuristicPredictions(upcomingStations, recentReports)
        }

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val stationNames = upcomingStations.joinToString(", ") { it.name }
            val reportsSummary = recentReports.take(15).joinToString("; ") {
                "${it.nearestStation}: ${it.crowdLevel} (${it.direction})"
            }

            val prompt = """
                You are Transit Saathi's AI crowd predictor for Mumbai local trains.
                Current line: ${currentStation.line}
                Current station: ${currentStation.name}
                Direction: ${direction.name}
                Upcoming stations: $stationNames
                Recent crowd reports: $reportsSummary
                Current time: Mumbai suburban peak/non-peak transit conditions.
                
                Respond ONLY with a JSON array predicting crowd density for the upcoming stations in this exact JSON schema:
                [
                  {
                    "stationName": "Station Name",
                    "line": "${currentStation.line}",
                    "predictedDensity": "GREEN_SEATS" or "YELLOW_STAND" or "RED_CRUSH",
                    "confidence": "HIGH" or "MEDIUM" or "LOW",
                    "advice": "Short 1-sentence tip for commuters"
                  }
                ]
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.2)
                })
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(endpoint)
                .post(body)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseStr = response.body?.string() ?: ""
                val respObj = JSONObject(responseStr)
                val candidates = respObj.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")

                if (!text.isNullOrBlank()) {
                    val predictionsArray = JSONArray(text)
                    val result = mutableListOf<StationPrediction>()
                    for (i in 0 until predictionsArray.length()) {
                        val item = predictionsArray.getJSONObject(i)
                        result.add(
                            StationPrediction(
                                stationName = item.optString("stationName", "Station"),
                                line = item.optString("line", currentStation.line),
                                predictedDensity = item.optString("predictedDensity", "YELLOW_STAND"),
                                confidence = item.optString("confidence", "HIGH"),
                                advice = item.optString("advice", "Standard headway expected")
                            )
                        )
                    }
                    if (result.isNotEmpty()) return@withContext result
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiTransitService", "Gemini API call failed, falling back to heuristic: ${e.message}")
        }

        return@withContext generateHeuristicPredictions(upcomingStations, recentReports)
    }

    private fun generateHeuristicPredictions(
        upcomingStations: List<Station>,
        recentReports: List<CrowdReport>
    ): List<StationPrediction> {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val isMorningPeak = hour in 8..11
        val isEveningPeak = hour in 17..21

        return upcomingStations.mapIndexed { idx, station ->
            // Major interchange stations generally experience denser crowds
            val isMajorHub = station.name in listOf("Dadar", "Dadar Western", "Dadar Central", "Andheri", "Kurla", "Thane", "Borivali", "Kalyan Junction")
            
            // Check if there is a recent report for this station
            val reportForStation = recentReports.firstOrNull { it.nearestStation.equals(station.name, ignoreCase = true) }

            val density = when {
                reportForStation != null -> reportForStation.crowdLevel
                isMajorHub && (isMorningPeak || isEveningPeak) -> "RED_CRUSH"
                isMorningPeak || isEveningPeak -> if (idx % 2 == 0) "RED_CRUSH" else "YELLOW_STAND"
                isMajorHub -> "YELLOW_STAND"
                else -> "GREEN_SEATS"
            }

            val advice = when (density) {
                "RED_CRUSH" -> if (isMajorHub) "Heavy rush expected at interchange FOB and platform doors" else "Board with care; avoid standing near footboard"
                "YELLOW_STAND" -> "Moderate rush; middle compartments have decent standing room"
                else -> "Comfortable travel; seats likely available in general coaches"
            }

            StationPrediction(
                stationName = station.name,
                line = station.line,
                predictedDensity = density,
                confidence = if (reportForStation != null) "VERY HIGH" else "HIGH",
                advice = advice
            )
        }
    }
}
