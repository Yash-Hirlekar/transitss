package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.EmergencyAlert
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EmergencyRepository(private val context: Context) {

    private val db: FirebaseFirestore by lazy {
        val dbId = context.getString(R.string.firestore_database_id)
        FirebaseFirestore.getInstance(dbId)
    }

    private val auth = Firebase.auth

    suspend fun createEmergencyAlert(alert: EmergencyAlert): Result<String> {
        val uid = auth.currentUser?.uid ?: "user_sos"
        val userName = auth.currentUser?.displayName ?: "Commuter"
        val emergencyId = db.collection("emergencies").document().id

        val payload = hashMapOf<String, Any>(
            "emergencyId" to emergencyId,
            "userId" to uid,
            "userName" to userName,
            "timestamp" to FieldValue.serverTimestamp(),
            "latitude" to alert.latitude,
            "longitude" to alert.longitude,
            "nearestStation" to alert.nearestStation,
            "nextStationPredicted" to alert.nextStationPredicted,
            "heading" to alert.heading,
            "speedKmh" to alert.speedKmh,
            "emergencyType" to alert.emergencyType,
            "status" to "ACTIVE",
            "resolved" to false
        )

        return try {
            db.collection("emergencies").document(emergencyId).set(payload).await()
            Log.d("EmergencyRepo", "Emergency ping created with id: $emergencyId")
            Result.success(emergencyId)
        } catch (e: Exception) {
            Log.e("EmergencyRepo", "Failed to log emergency: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun cancelEmergency(emergencyId: String): Result<Boolean> {
        return try {
            db.collection("emergencies").document(emergencyId)
                .update(
                    mapOf(
                        "status" to "CANCELLED",
                        "resolved" to true
                    )
                ).await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("EmergencyRepo", "Failed to cancel emergency: ${e.message}", e)
            Result.failure(e)
        }
    }
}
