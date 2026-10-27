package com.example.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.R
import com.example.data.model.CrowdReport
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

class CrowdRepository(private val context: Context) {

    private val db: FirebaseFirestore by lazy {
        val dbId = context.getString(R.string.firestore_database_id)
        FirebaseFirestore.getInstance(dbId)
    }

    private val auth = Firebase.auth
    private val prefs = context.getSharedPreferences("transit_saathi_offline_reports", Context.MODE_PRIVATE)

    fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before submitting crowd reports.")
    }

    fun observeRecentReports(limit: Long = 50): Flow<List<CrowdReport>> = flow {
        if (auth.currentUser == null) {
            emit(emptyList())
            return@flow
        }
        val reportsRef = db.collection("crowd_reports")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit)

        reportsRef.snapshots().map { snapshot ->
            snapshot.toObjects(CrowdReport::class.java)
        }.catch { error ->
            Log.e("CrowdRepository", "Error observing crowd reports: ${error.message}", error)
            emit(emptyList())
        }.collect {
            emit(it)
        }
    }

    suspend fun submitReport(report: CrowdReport): Result<Boolean> {
        val uid = auth.currentUser?.uid ?: "anonymous_user"
        val userName = auth.currentUser?.displayName ?: "Commuter"
        val reportId = db.collection("crowd_reports").document().id

        val payload = hashMapOf<String, Any>(
            "reportId" to reportId,
            "userId" to uid,
            "userName" to userName,
            "timestamp" to FieldValue.serverTimestamp(),
            "latitude" to report.latitude,
            "longitude" to report.longitude,
            "nearestStation" to report.nearestStation,
            "line" to report.line,
            "speedKmh" to report.speedKmh,
            "direction" to report.direction,
            "crowdLevel" to report.crowdLevel,
            "crowdCategory" to report.crowdCategory,
            "isSynced" to isOnline()
        )

        if (!isOnline()) {
            // Save to offline queue
            saveReportLocally(report.copy(reportId = reportId, userId = uid, userName = userName, isSynced = false))
            return Result.success(true)
        }

        return try {
            db.collection("crowd_reports").document(reportId).set(payload).await()
            // After successful online write, attempt to flush any pending offline reports
            syncPendingReports()
            Result.success(true)
        } catch (e: Exception) {
            Log.w("CrowdRepository", "Online submit failed, saving locally: ${e.message}")
            saveReportLocally(report.copy(reportId = reportId, userId = uid, userName = userName, isSynced = false))
            Result.success(true)
        }
    }

    private fun saveReportLocally(report: CrowdReport) {
        try {
            val existing = prefs.getString("queue", "[]") ?: "[]"
            val array = JSONArray(existing)
            val obj = JSONObject().apply {
                put("reportId", report.reportId)
                put("userId", report.userId)
                put("userName", report.userName)
                put("latitude", report.latitude)
                put("longitude", report.longitude)
                put("nearestStation", report.nearestStation)
                put("line", report.line)
                put("speedKmh", report.speedKmh)
                put("direction", report.direction)
                put("crowdLevel", report.crowdLevel)
                put("crowdCategory", report.crowdCategory)
                put("isSynced", false)
            }
            array.put(obj)
            prefs.edit().putString("queue", array.toString()).apply()
            Log.d("CrowdRepository", "Saved offline report: ${report.nearestStation}")
        } catch (e: Exception) {
            Log.e("CrowdRepository", "Failed to cache report locally", e)
        }
    }

    fun syncPendingReports() {
        if (!isOnline()) return
        val queueStr = prefs.getString("queue", "[]") ?: "[]"
        if (queueStr == "[]") return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val array = JSONArray(queueStr)
                if (array.length() == 0) return@launch
                val remaining = JSONArray()

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val reportId = obj.optString("reportId", db.collection("crowd_reports").document().id)
                    val payload = hashMapOf<String, Any>(
                        "reportId" to reportId,
                        "userId" to obj.optString("userId", auth.currentUser?.uid ?: "user"),
                        "userName" to obj.optString("userName", "Commuter"),
                        "timestamp" to FieldValue.serverTimestamp(),
                        "latitude" to obj.optDouble("latitude", 19.0),
                        "longitude" to obj.optDouble("longitude", 72.8),
                        "nearestStation" to obj.optString("nearestStation", "Mumbai"),
                        "line" to obj.optString("line", "Western"),
                        "speedKmh" to obj.optDouble("speedKmh", 0.0),
                        "direction" to obj.optString("direction", "UNKNOWN"),
                        "crowdLevel" to obj.optString("crowdLevel", "YELLOW_STAND"),
                        "crowdCategory" to obj.optString("crowdCategory", "TRAIN"),
                        "isSynced" to true
                    )
                    try {
                        db.collection("crowd_reports").document(reportId).set(payload).await()
                    } catch (e: Exception) {
                        remaining.put(obj)
                    }
                }
                prefs.edit().putString("queue", remaining.toString()).apply()
                Log.d("CrowdRepository", "Offline sync completed, remaining items: ${remaining.length()}")
            } catch (e: Exception) {
                Log.e("CrowdRepository", "Error syncing offline queue", e)
            }
        }
    }

    fun getPendingCount(): Int {
        val str = prefs.getString("queue", "[]") ?: "[]"
        return try {
            JSONArray(str).length()
        } catch (e: Exception) {
            0
        }
    }
}
