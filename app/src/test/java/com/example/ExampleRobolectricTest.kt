package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.StationRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Transit Saathi", appName)
    }

    @Test
    fun `test nearest station detection with haversine formula`() {
        // Churchgate station coordinates
        val churchgate = StationRepository.findNearestStation(18.9355, 72.8272)
        assertEquals("Churchgate", churchgate.name)

        // Dadar area coordinates
        val dadar = StationRepository.findNearestStation(19.0183, 72.8432)
        assertTrue(dadar.name.contains("Dadar"))
    }
}
