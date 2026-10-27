package com.example.data.repository

import com.example.data.model.Station
import com.example.data.model.StationStopTime
import com.example.data.model.TrainDirection
import com.example.data.model.TrainSchedule
import com.example.data.model.TrainSpeedType
import java.text.SimpleDateFormat
import java.util.*

object TimetableRepository {

    // All Suburban Lines
    val lines = listOf("Western", "Central", "Harbour", "Trans-Harbour", "Uran")

    // Full Schedule Dataset parsed from Western, Central, Harbour, Trans-Harbour and Uran PDFs
    val allSchedules: List<TrainSchedule> by lazy {
        buildAllSchedules()
    }

    private fun buildAllSchedules(): List<TrainSchedule> {
        val list = mutableListOf<TrainSchedule>()

        // =========================================================================
        // 1. WESTERN LINE (Churchgate <-> Borivali / Virar / Dahanu Road)
        // =========================================================================

        // --- WESTERN DOWN (Churchgate to Suburbs) ---
        fun wrDown(num: String, code: String, dest: String, destCode: String, dep: String, arr: String, speed: TrainSpeedType, ac: Boolean = false, c15: Boolean = false, ladies: Boolean = false, pf: String = "PF 1", days: String = "Daily", stops: List<Pair<String, String>>) =
            TrainSchedule(
                trainNumber = num, trainCode = code, line = "Western", direction = TrainDirection.DOWN,
                originStation = "Churchgate", destinationStation = dest, destinationCode = destCode,
                departureTime = dep, arrivalTime = arr, speedType = speed, isAC = ac, is15Car = c15,
                isLadiesSpecial = ladies, platform = pf, runDays = days,
                stoppages = stops.map { StationStopTime(it.first, it.first.take(3).uppercase(), it.second, pf) }
            )

        list.addAll(listOf(
            wrDown("93001", "DRD 1", "Dahanu Road", "DRD", "04:15", "06:50", TrainSpeedType.FAST, pf = "PF 3", stops = listOf(
                "Churchgate" to "04:15", "Dadar Western" to "04:36", "Bandra" to "04:45", "Andheri" to "04:58", "Borivali" to "05:20", "Bhayandar" to "05:34", "Vasai Road" to "05:45", "Virar" to "05:58", "Palghar" to "06:20", "Boisar" to "06:33", "Dahanu Road" to "06:50"
            )),
            wrDown("90001", "BVI 1", "Borivali", "BVI", "04:18", "05:22", TrainSpeedType.SLOW, pf = "PF 2", stops = listOf(
                "Churchgate" to "04:18", "Marine Lines" to "04:21", "Charni Road" to "04:23", "Grant Road" to "04:26", "Mumbai Central" to "04:28", "Dadar Western" to "04:39", "Bandra" to "04:48", "Andheri" to "05:01", "Goregaon" to "05:09", "Borivali" to "05:22"
            )),
            wrDown("92001", "VR 1", "Virar", "VR", "04:20", "05:45", TrainSpeedType.FAST, c15 = true, pf = "PF 4", stops = listOf(
                "Churchgate" to "04:20", "Mumbai Central" to "04:31", "Dadar Western" to "04:38", "Bandra" to "04:45", "Andheri" to "04:54", "Borivali" to "05:07", "Bhayandar" to "05:21", "Vasai Road" to "05:32", "Virar" to "05:45"
            )),
            wrDown("94001", "VR AC 1", "Virar", "VR", "04:40", "06:02", TrainSpeedType.FAST, ac = true, pf = "PF 3", stops = listOf(
                "Churchgate" to "04:40", "Mumbai Central" to "04:50", "Dadar Western" to "04:57", "Bandra" to "05:03", "Andheri" to "05:12", "Borivali" to "05:26", "Bhayandar" to "05:40", "Vasai Road" to "05:50", "Virar" to "06:02"
            )),
            wrDown("90019", "BVI 3", "Borivali", "BVI", "04:45", "05:48", TrainSpeedType.SLOW, pf = "PF 1", stops = listOf(
                "Churchgate" to "04:45", "Mumbai Central" to "04:55", "Dadar Western" to "05:06", "Bandra" to "05:15", "Andheri" to "05:27", "Goregaon" to "05:35", "Borivali" to "05:48"
            )),
            wrDown("94003", "BVI AC 3", "Borivali", "BVI", "05:35", "06:30", TrainSpeedType.FAST, ac = true, pf = "PF 4", stops = listOf(
                "Churchgate" to "05:35", "Mumbai Central" to "05:45", "Dadar Western" to "05:52", "Bandra" to "05:59", "Andheri" to "06:08", "Borivali" to "06:30"
            )),
            wrDown("92011", "VR 15C", "Virar", "VR", "05:15", "06:20", TrainSpeedType.FAST, c15 = true, pf = "PF 3", stops = listOf(
                "Churchgate" to "05:15", "Dadar Western" to "05:26", "Bandra" to "05:32", "Andheri" to "05:40", "Borivali" to "05:52", "Bhayandar" to "06:04", "Vasai Road" to "06:12", "Virar" to "06:20"
            )),
            wrDown("90033", "VR 3", "Virar", "VR", "05:07", "06:30", TrainSpeedType.SLOW, pf = "PF 2", stops = listOf(
                "Churchgate" to "05:07", "Dadar Western" to "05:24", "Bandra" to "05:30", "Andheri" to "05:40", "Borivali" to "05:54", "Bhayandar" to "06:07", "Vasai Road" to "06:17", "Virar" to "06:30"
            )),
            wrDown("94007", "BVI AC 5", "Borivali", "BVI", "06:14", "07:19", TrainSpeedType.FAST, ac = true, pf = "PF 1", stops = listOf(
                "Churchgate" to "06:14", "Mumbai Central" to "06:24", "Dadar Western" to "06:35", "Bandra" to "06:44", "Andheri" to "06:57", "Borivali" to "07:19"
            )),
            wrDown("90071", "BVI 7", "Borivali", "BVI", "05:56", "07:00", TrainSpeedType.SLOW, pf = "PF 2", stops = listOf(
                "Churchgate" to "05:56", "Mumbai Central" to "06:06", "Dadar Western" to "06:17", "Bandra" to "06:26", "Andheri" to "06:39", "Borivali" to "07:00"
            )),
            wrDown("92021", "VR 15C 5", "Virar", "VR", "06:00", "07:22", TrainSpeedType.FAST, c15 = true, pf = "PF 4", stops = listOf(
                "Churchgate" to "06:00", "Mumbai Central" to "06:10", "Dadar Western" to "06:17", "Bandra" to "06:22", "Andheri" to "06:30", "Borivali" to "06:42", "Bhayandar" to "06:57", "Vasai Road" to "07:08", "Virar" to "07:22"
            )),
            wrDown("94017", "VR AC 7", "Virar", "VR", "06:50", "08:23", TrainSpeedType.FAST, ac = true, pf = "PF 3", stops = listOf(
                "Churchgate" to "06:50", "Dadar Western" to "07:07", "Bandra" to "07:13", "Andheri" to "07:27", "Borivali" to "07:47", "Bhayandar" to "08:01", "Vasai Road" to "08:11", "Virar" to "08:23"
            )),
            wrDown("94025", "VR AC 9", "Virar", "VR", "07:36", "08:58", TrainSpeedType.FAST, ac = true, pf = "PF 4", stops = listOf(
                "Churchgate" to "07:36", "Mumbai Central" to "07:46", "Dadar Western" to "07:53", "Bandra" to "07:59", "Andheri" to "08:07", "Borivali" to "08:21", "Bhayandar" to "08:35", "Vasai Road" to "08:46", "Virar" to "08:58"
            )),
            wrDown("90135", "BSR 9", "Vasai Road", "BSR", "07:16", "08:26", TrainSpeedType.SLOW, pf = "PF 1", stops = listOf(
                "Churchgate" to "07:16", "Dadar Western" to "07:33", "Bandra" to "07:39", "Andheri" to "07:48", "Borivali" to "08:02", "Bhayandar" to "08:16", "Vasai Road" to "08:26"
            )),
            wrDown("94033", "VR AC 11", "Virar", "VR", "08:53", "10:12", TrainSpeedType.FAST, ac = true, pf = "PF 3", stops = listOf(
                "Churchgate" to "08:53", "Dadar Western" to "09:09", "Bandra" to "09:14", "Andheri" to "09:22", "Borivali" to "09:36", "Bhayandar" to "09:50", "Vasai Road" to "10:01", "Virar" to "10:12"
            )),
            wrDown("90201", "VR 17", "Virar", "VR", "08:20", "09:48", TrainSpeedType.SLOW, pf = "PF 2", stops = listOf(
                "Churchgate" to "08:20", "Dadar Western" to "08:39", "Bandra" to "08:45", "Andheri" to "08:55", "Borivali" to "09:09", "Bhayandar" to "09:24", "Vasai Road" to "09:35", "Virar" to "09:48"
            )),
            wrDown("90273", "VR 21", "Virar", "VR", "09:36", "10:57", TrainSpeedType.FAST, pf = "PF 4", stops = listOf(
                "Churchgate" to "09:36", "Dadar Western" to "09:53", "Bandra" to "09:58", "Andheri" to "10:06", "Borivali" to "10:21", "Bhayandar" to "10:34", "Vasai Road" to "10:45", "Virar" to "10:57"
            )),
            wrDown("90777", "VR L-SPL", "Virar", "VR", "18:51", "20:40", TrainSpeedType.FAST, ladies = true, pf = "PF 3", stops = listOf(
                "Churchgate" to "18:51", "Mumbai Central" to "19:01", "Dadar Western" to "19:12", "Bandra" to "19:21", "Andheri" to "19:37", "Borivali" to "19:59", "Bhayandar" to "20:14", "Vasai Road" to "20:25", "Virar" to "20:40"
            )),
            wrDown("94119", "VR AC EVE", "Virar", "VR", "19:00", "20:22", TrainSpeedType.FAST, ac = true, pf = "PF 4", stops = listOf(
                "Churchgate" to "19:00", "Dadar Western" to "19:18", "Bandra" to "19:23", "Andheri" to "19:31", "Borivali" to "19:44", "Bhayandar" to "19:59", "Vasai Road" to "20:10", "Virar" to "20:22"
            )),
            wrDown("90829", "VR L-EVE", "Virar", "VR", "19:40", "21:06", TrainSpeedType.FAST, ladies = true, pf = "PF 2", stops = listOf(
                "Churchgate" to "19:40", "Dadar Western" to "19:58", "Bandra" to "20:03", "Andheri" to "20:12", "Borivali" to "20:27", "Bhayandar" to "20:43", "Vasai Road" to "20:54", "Virar" to "21:06"
            )),
            wrDown("91015", "VR NIGHT", "Virar", "VR", "23:23", "00:57", TrainSpeedType.FAST, pf = "PF 3", stops = listOf(
                "Churchgate" to "23:23", "Dadar Western" to "23:40", "Bandra" to "23:46", "Andheri" to "23:56", "Borivali" to "00:20", "Bhayandar" to "00:34", "Vasai Road" to "00:45", "Virar" to "00:57"
            )),
            wrDown("91055", "BVI LATE", "Borivali", "BVI", "01:00", "02:05", TrainSpeedType.SLOW, pf = "PF 1", stops = listOf(
                "Churchgate" to "01:00", "Mumbai Central" to "01:10", "Dadar Western" to "01:21", "Bandra" to "01:30", "Andheri" to "01:44", "Borivali" to "02:05"
            ))
        ))

        // --- WESTERN UP (Suburbs to Churchgate) ---
        fun wrUp(num: String, code: String, orig: String, dep: String, arr: String, speed: TrainSpeedType, ac: Boolean = false, c15: Boolean = false, ladies: Boolean = false, pf: String = "PF 1", days: String = "Daily", stops: List<Pair<String, String>>) =
            TrainSchedule(
                trainNumber = num, trainCode = code, line = "Western", direction = TrainDirection.UP,
                originStation = orig, destinationStation = "Churchgate", destinationCode = "CCG",
                departureTime = dep, arrivalTime = arr, speedType = speed, isAC = ac, is15Car = c15,
                isLadiesSpecial = ladies, platform = pf, runDays = days,
                stoppages = stops.map { StationStopTime(it.first, it.first.take(3).uppercase(), it.second, pf) }
            )

        list.addAll(listOf(
            wrUp("90006", "VR 2", "Virar", "03:25", "05:05", TrainSpeedType.SLOW, pf = "PF 1", stops = listOf(
                "Virar" to "03:25", "Vasai Road" to "03:36", "Bhayandar" to "03:47", "Borivali" to "04:00", "Andheri" to "04:20", "Bandra" to "04:33", "Dadar Western" to "04:42", "Mumbai Central" to "04:54", "Churchgate" to "05:05"
            )),
            wrUp("94002", "NSP AC 2", "Nallasopara", "04:00", "05:36", TrainSpeedType.FAST, ac = true, pf = "PF 2", stops = listOf(
                "Nallasopara" to "04:00", "Vasai Road" to "04:06", "Bhayandar" to "04:16", "Borivali" to "04:29", "Andheri" to "04:51", "Bandra" to "05:05", "Dadar Western" to "05:14", "Mumbai Central" to "05:26", "Churchgate" to "05:36"
            )),
            wrUp("92002", "VR 15C 2", "Virar", "04:00", "05:32", TrainSpeedType.FAST, c15 = true, pf = "PF 3", stops = listOf(
                "Virar" to "04:00", "Vasai Road" to "04:10", "Bhayandar" to "04:18", "Borivali" to "04:28", "Andheri" to "04:43", "Bandra" to "04:53", "Dadar Western" to "05:02", "Mumbai Central" to "05:21", "Churchgate" to "05:32"
            )),
            wrUp("90054", "VR 4", "Virar", "04:35", "06:03", TrainSpeedType.FAST, pf = "PF 1", stops = listOf(
                "Virar" to "04:35", "Vasai Road" to "04:46", "Bhayandar" to "04:57", "Borivali" to "05:11", "Andheri" to "05:26", "Bandra" to "05:36", "Dadar Western" to "05:42", "Mumbai Central" to "05:50", "Churchgate" to "06:03"
            )),
            wrUp("94008", "NSP AC 4", "Nallasopara", "04:50", "06:29", TrainSpeedType.FAST, ac = true, pf = "PF 2", stops = listOf(
                "Nallasopara" to "04:50", "Vasai Road" to "04:55", "Bhayandar" to "05:06", "Borivali" to "05:20", "Andheri" to "05:41", "Bandra" to "05:54", "Dadar Western" to "06:03", "Mumbai Central" to "06:15", "Churchgate" to "06:29"
            )),
            wrUp("90070", "VR 6", "Virar", "05:11", "06:39", TrainSpeedType.FAST, pf = "PF 3", stops = listOf(
                "Virar" to "05:11", "Vasai Road" to "05:22", "Bhayandar" to "05:33", "Borivali" to "05:47", "Andheri" to "06:01", "Bandra" to "06:11", "Dadar Western" to "06:19", "Mumbai Central" to "06:27", "Churchgate" to "06:39"
            )),
            wrUp("94014", "VR AC 6", "Virar", "05:57", "07:22", TrainSpeedType.FAST, ac = true, pf = "PF 4", stops = listOf(
                "Virar" to "05:57", "Vasai Road" to "06:08", "Bhayandar" to "06:19", "Borivali" to "06:36", "Andheri" to "06:50", "Bandra" to "06:59", "Dadar Western" to "07:04", "Mumbai Central" to "07:11", "Churchgate" to "07:22"
            )),
            wrUp("90098", "VR 8", "Virar", "05:39", "07:02", TrainSpeedType.FAST, pf = "PF 1", stops = listOf(
                "Virar" to "05:39", "Vasai Road" to "05:52", "Bhayandar" to "06:03", "Borivali" to "06:19", "Andheri" to "06:26", "Bandra" to "06:35", "Dadar Western" to "06:41", "Mumbai Central" to "06:48", "Churchgate" to "07:02"
            )),
            wrUp("90148", "BVI 12", "Borivali", "07:13", "08:08", TrainSpeedType.SLOW, pf = "PF 2", stops = listOf(
                "Borivali" to "07:13", "Kandivali" to "07:19", "Malad" to "07:22", "Goregaon" to "07:25", "Andheri" to "07:34", "Bandra" to "07:42", "Dadar Western" to "07:48", "Mumbai Central" to "07:56", "Churchgate" to "08:08"
            )),
            wrUp("90172", "BVI L-SPL", "Borivali", "07:38", "08:46", TrainSpeedType.SLOW, ladies = true, pf = "PF 1", stops = listOf(
                "Borivali" to "07:38", "Kandivali" to "07:44", "Malad" to "07:47", "Goregaon" to "07:51", "Andheri" to "08:00", "Bandra" to "08:15", "Dadar Western" to "08:25", "Mumbai Central" to "08:36", "Churchgate" to "08:46"
            )),
            wrUp("94024", "VR AC 8", "Virar", "07:15", "08:49", TrainSpeedType.FAST, ac = true, pf = "PF 3", stops = listOf(
                "Virar" to "07:15", "Vasai Road" to "07:26", "Bhayandar" to "07:37", "Borivali" to "07:53", "Andheri" to "08:15", "Bandra" to "08:24", "Dadar Western" to "08:30", "Mumbai Central" to "08:37", "Churchgate" to "08:49"
            )),
            wrUp("92036", "VR 15C 8", "Virar", "07:18", "08:49", TrainSpeedType.FAST, c15 = true, pf = "PF 4", stops = listOf(
                "Virar" to "07:18", "Vasai Road" to "07:27", "Bhayandar" to "07:35", "Borivali" to "07:49", "Andheri" to "08:00", "Bandra" to "08:09", "Dadar Western" to "08:16", "Mumbai Central" to "08:25", "Churchgate" to "08:49"
            )),
            wrUp("90200", "VR L-SPL 2", "Virar", "07:35", "09:15", TrainSpeedType.FAST, ladies = true, pf = "PF 2", stops = listOf(
                "Virar" to "07:35", "Vasai Road" to "07:46", "Bhayandar" to "07:57", "Borivali" to "08:11", "Andheri" to "08:31", "Bandra" to "08:44", "Dadar Western" to "08:54", "Mumbai Central" to "09:05", "Churchgate" to "09:15"
            )),
            wrUp("94042", "VR AC 10", "Virar", "09:23", "10:50", TrainSpeedType.FAST, ac = true, pf = "PF 3", stops = listOf(
                "Virar" to "09:23", "Vasai Road" to "09:35", "Bhayandar" to "09:45", "Borivali" to "10:00", "Andheri" to "10:15", "Bandra" to "10:25", "Dadar Western" to "10:31", "Mumbai Central" to "10:38", "Churchgate" to "10:50"
            )),
            wrUp("93018", "DRD 4", "Dahanu Road", "12:53", "15:46", TrainSpeedType.FAST, pf = "PF 4", stops = listOf(
                "Dahanu Road" to "12:53", "Boisar" to "13:15", "Palghar" to "13:27", "Virar" to "13:57", "Vasai Road" to "14:07", "Borivali" to "14:24", "Andheri" to "14:36", "Dadar Western" to "14:51", "Churchgate" to "15:09"
            )),
            wrUp("94132", "VR AC 16", "Virar", "20:07", "21:32", TrainSpeedType.FAST, ac = true, pf = "PF 3", stops = listOf(
                "Virar" to "20:07", "Vasai Road" to "20:19", "Bhayandar" to "20:29", "Borivali" to "20:45", "Andheri" to "20:58", "Bandra" to "21:06", "Dadar Western" to "21:14", "Mumbai Central" to "21:21", "Churchgate" to "21:32"
            )),
            wrUp("91026", "VR LATE", "Virar", "00:05", "01:45", TrainSpeedType.SLOW, pf = "PF 1", stops = listOf(
                "Virar" to "00:05", "Vasai Road" to "00:16", "Bhayandar" to "00:26", "Borivali" to "00:39", "Andheri" to "01:00", "Bandra" to "01:13", "Dadar Western" to "01:23", "Mumbai Central" to "01:34", "Churchgate" to "01:45"
            ))
        ))

        // =========================================================================
        // 2. CENTRAL LINE (CSMT <-> Thane / Kalyan / Kasara / Khopoli)
        // =========================================================================

        // --- CENTRAL DOWN ---
        fun crDown(num: String, code: String, dest: String, destCode: String, dep: String, arr: String, speed: TrainSpeedType, ac: Boolean = false, c15: Boolean = false, pf: String = "PF 1", stops: List<Pair<String, String>>) =
            TrainSchedule(
                trainNumber = num, trainCode = code, line = "Central", direction = TrainDirection.DOWN,
                originStation = "CSMT", destinationStation = dest, destinationCode = destCode,
                departureTime = dep, arrivalTime = arr, speedType = speed, isAC = ac, is15Car = c15,
                platform = pf,
                stoppages = stops.map { StationStopTime(it.first, it.first.take(3).uppercase(), it.second, pf) }
            )

        list.addAll(listOf(
            crDown("96301", "A 1", "Ambernath", "A", "00:02", "01:46", TrainSpeedType.SLOW, pf = "PF 1", stops = listOf(
                "CSMT" to "00:02", "Byculla" to "00:10", "Dadar Central" to "00:20", "Kurla" to "00:30", "Ghatkopar" to "00:36", "Thane" to "00:56", "Dombivli" to "01:20", "Kalyan" to "01:30", "Ambernath" to "01:46"
            )),
            crDown("96101", "S 1", "Kasara", "S", "00:12", "02:54", TrainSpeedType.FAST, pf = "PF 4", stops = listOf(
                "CSMT" to "00:12", "Byculla" to "00:20", "Dadar Central" to "00:30", "Kurla" to "00:40", "Ghatkopar" to "00:46", "Thane" to "01:06", "Dombivli" to "01:26", "Kalyan" to "01:37", "Titwala" to "01:50", "Asangaon" to "02:10", "Kasara" to "02:54"
            )),
            crDown("95001", "KP 1", "Khopoli", "KP", "04:35", "07:09", TrainSpeedType.FAST, pf = "PF 5", stops = listOf(
                "CSMT" to "04:35", "Byculla" to "04:42", "Dadar Central" to "04:48", "Kurla" to "04:57", "Ghatkopar" to "05:02", "Thane" to "05:20", "Dombivli" to "05:36", "Kalyan" to "05:48", "Ambernath" to "06:04", "Badlapur" to "06:11", "Karjat" to "06:44", "Khopoli" to "07:09"
            )),
            crDown("95701", "K 3 AC", "Kalyan", "K", "05:20", "06:24", TrainSpeedType.FAST, ac = true, pf = "PF 4", stops = listOf(
                "CSMT" to "05:20", "Byculla" to "05:27", "Dadar Central" to "05:33", "Kurla" to "05:40", "Ghatkopar" to "05:44", "Thane" to "06:01", "Dombivli" to "06:17", "Kalyan" to "06:24"
            )),
            crDown("97009", "CK 3 AC", "Kalyan", "K", "05:46", "07:04", TrainSpeedType.FAST, ac = true, pf = "PF 6", stops = listOf(
                "CSMT" to "05:46", "Dadar Central" to "05:59", "Kurla" to "06:06", "Ghatkopar" to "06:10", "Thane" to "06:25", "Dombivli" to "06:41", "Kalyan" to "07:04"
            )),
            crDown("95703", "K 5 15C", "Kalyan", "K", "06:06", "07:10", TrainSpeedType.FAST, c15 = true, pf = "PF 5", stops = listOf(
                "CSMT" to "06:06", "Byculla" to "06:14", "Dadar Central" to "06:21", "Kurla" to "06:28", "Ghatkopar" to "06:32", "Thane" to "06:46", "Dombivli" to "07:02", "Kalyan" to "07:10"
            )),
            crDown("97013", "CK 7 AC", "Kalyan", "K", "06:10", "07:25", TrainSpeedType.FAST, ac = true, pf = "PF 4", stops = listOf(
                "CSMT" to "06:10", "Dadar Central" to "06:24", "Kurla" to "06:31", "Ghatkopar" to "06:35", "Thane" to "06:50", "Dombivli" to "07:14", "Kalyan" to "07:25"
            )),
            crDown("96607", "TL 3", "Thane", "TL", "06:01", "06:46", TrainSpeedType.SLOW, pf = "PF 1", stops = listOf(
                "CSMT" to "06:01", "Byculla" to "06:09", "Dadar Central" to "06:19", "Kurla" to "06:29", "Ghatkopar" to "06:35", "Bhandup" to "06:45", "Mulund" to "06:51", "Thane" to "06:46"
            )),
            crDown("96611", "TL 7 AC", "Thane", "TL", "06:30", "07:25", TrainSpeedType.FAST, ac = true, pf = "PF 4", stops = listOf(
                "CSMT" to "06:30", "Byculla" to "06:38", "Dadar Central" to "06:48", "Kurla" to "06:58", "Ghatkopar" to "07:04", "Thane" to "07:25"
            )),
            crDown("95707", "K 17 AC", "Kalyan", "K", "07:43", "08:46", TrainSpeedType.FAST, ac = true, pf = "PF 5", stops = listOf(
                "CSMT" to "07:43", "Byculla" to "07:50", "Dadar Central" to "07:56", "Kurla" to "08:03", "Ghatkopar" to "08:07", "Thane" to "08:23", "Dombivli" to "08:39", "Kalyan" to "08:46"
            )),
            crDown("95901", "T 15 AC", "Titwala", "T", "08:04", "09:45", TrainSpeedType.FAST, ac = true, pf = "PF 4", stops = listOf(
                "CSMT" to "08:04", "Byculla" to "08:11", "Dadar Central" to "08:17", "Kurla" to "08:24", "Ghatkopar" to "08:28", "Thane" to "08:46", "Dombivli" to "09:07", "Kalyan" to "09:17", "Titwala" to "09:45"
            )),
            crDown("97041", "K 25 AC", "Kalyan", "K", "08:49", "09:56", TrainSpeedType.FAST, ac = true, pf = "PF 6", stops = listOf(
                "CSMT" to "08:49", "Byculla" to "08:57", "Dadar Central" to "09:03", "Kurla" to "09:10", "Ghatkopar" to "09:18", "Thane" to "09:34", "Dombivli" to "09:49", "Kalyan" to "09:56"
            )),
            crDown("95711", "K 27 AC", "Kalyan", "K", "08:53", "09:58", TrainSpeedType.FAST, ac = true, pf = "PF 5", stops = listOf(
                "CSMT" to "08:53", "Byculla" to "09:01", "Dadar Central" to "09:07", "Kurla" to "09:14", "Ghatkopar" to "09:21", "Thane" to "09:37", "Dombivli" to "09:52", "Kalyan" to "09:58"
            )),
            crDown("97341", "T 43", "Titwala", "T", "10:18", "11:41", TrainSpeedType.SLOW, pf = "PF 2", stops = listOf(
                "CSMT" to "10:18", "Byculla" to "10:26", "Dadar Central" to "10:36", "Kurla" to "10:46", "Ghatkopar" to "10:52", "Thane" to "11:13", "Dombivli" to "11:32", "Kalyan" to "11:42", "Titwala" to "12:00"
            )),
            crDown("95733", "K 107 AC", "Kalyan", "K", "18:36", "19:41", TrainSpeedType.FAST, ac = true, pf = "PF 4", stops = listOf(
                "CSMT" to "18:36", "Byculla" to "18:43", "Dadar Central" to "18:49", "Kurla" to "18:56", "Ghatkopar" to "19:00", "Thane" to "19:18", "Dombivli" to "19:34", "Kalyan" to "19:41"
            )),
            crDown("97403", "T 105 AC", "Titwala", "T", "18:45", "20:01", TrainSpeedType.FAST, ac = true, pf = "PF 5", stops = listOf(
                "CSMT" to "18:45", "Byculla" to "18:53", "Dadar Central" to "19:03", "Kurla" to "19:13", "Ghatkopar" to "19:19", "Thane" to "19:42", "Dombivli" to "19:48", "Kalyan" to "19:53", "Titwala" to "20:01"
            )),
            crDown("97143", "K 103", "Kalyan", "K", "18:07", "19:10", TrainSpeedType.FAST, pf = "PF 4", stops = listOf(
                "CSMT" to "18:07", "Byculla" to "18:15", "Dadar Central" to "18:26", "Kurla" to "18:37", "Ghatkopar" to "18:43", "Thane" to "19:05", "Dombivli" to "19:27", "Kalyan" to "19:37"
            )),
            crDown("96653", "TL 65", "Thane", "TL", "23:42", "00:41", TrainSpeedType.SLOW, pf = "PF 1", stops = listOf(
                "CSMT" to "23:42", "Byculla" to "23:50", "Dadar Central" to "00:00", "Kurla" to "00:10", "Ghatkopar" to "00:20", "Mulund" to "00:36", "Thane" to "00:41"
            ))
        ))

        // --- CENTRAL UP (Kasara/Khopoli/Karjat/Kalyan to CSMT) ---
        fun crUp(num: String, code: String, orig: String, dep: String, arr: String, speed: TrainSpeedType, ac: Boolean = false, c15: Boolean = false, pf: String = "PF 1", stops: List<Pair<String, String>>) =
            TrainSchedule(
                trainNumber = num, trainCode = code, line = "Central", direction = TrainDirection.UP,
                originStation = orig, destinationStation = "CSMT", destinationCode = "CSMT",
                departureTime = dep, arrivalTime = arr, speedType = speed, isAC = ac, is15Car = c15,
                platform = pf,
                stoppages = stops.map { StationStopTime(it.first, it.first.take(3).uppercase(), it.second, pf) }
            )

        list.addAll(listOf(
            crUp("96102", "S 2", "Kasara", "02:30", "05:00", TrainSpeedType.FAST, pf = "PF 3", stops = listOf(
                "Kasara" to "02:30", "Asangaon" to "03:10", "Titwala" to "03:56", "Kalyan" to "04:12", "Dombivli" to "04:21", "Thane" to "04:40", "Kurla" to "04:54", "Dadar Central" to "05:05", "CSMT" to "05:00"
            )),
            crUp("97604", "C 4 AC", "Kurla", "04:46", "05:16", TrainSpeedType.SLOW, ac = true, pf = "PF 2", stops = listOf(
                "Kurla" to "04:46", "Sion" to "04:50", "Dadar Central" to "04:57", "Byculla" to "05:07", "CSMT" to "05:16"
            )),
            crUp("97310", "T 12 AC", "Titwala", "04:32", "06:21", TrainSpeedType.FAST, ac = true, pf = "PF 4", stops = listOf(
                "Titwala" to "04:32", "Kalyan" to "04:48", "Dombivli" to "04:56", "Thane" to "05:24", "Kurla" to "05:50", "Dadar Central" to "06:01", "Byculla" to "06:12", "CSMT" to "06:21"
            )),
            crUp("96402", "N 2", "Kasara", "03:51", "06:30", TrainSpeedType.FAST, pf = "PF 3", stops = listOf(
                "Kasara" to "03:51", "Asangaon" to "04:28", "Titwala" to "04:48", "Kalyan" to "05:03", "Dombivli" to "05:11", "Thane" to "05:34", "Ghatkopar" to "05:54", "Kurla" to "06:00", "Dadar Central" to "06:11", "CSMT" to "06:30"
            )),
            crUp("95702", "K 12 AC", "Kalyan", "06:32", "07:39", TrainSpeedType.FAST, ac = true, pf = "PF 4", stops = listOf(
                "Kalyan" to "06:32", "Dombivli" to "06:37", "Thane" to "06:55", "Ghatkopar" to "07:11", "Kurla" to "07:15", "Dadar Central" to "07:24", "CSMT" to "07:39"
            )),
            crUp("97022", "K 16 AC", "Kalyan", "07:15", "08:45", TrainSpeedType.FAST, ac = true, pf = "PF 5", stops = listOf(
                "Kalyan" to "07:15", "Dombivli" to "07:24", "Thane" to "07:47", "Ghatkopar" to "08:09", "Kurla" to "08:15", "Dadar Central" to "08:26", "CSMT" to "08:45"
            )),
            crUp("97026", "K 22 AC", "Kalyan", "07:34", "08:58", TrainSpeedType.FAST, ac = true, pf = "PF 6", stops = listOf(
                "Kalyan" to "07:34", "Dombivli" to "07:43", "Thane" to "08:06", "Ghatkopar" to "08:27", "Kurla" to "08:33", "Dadar Central" to "08:44", "CSMT" to "08:58"
            )),
            crUp("95904", "T 28 AC", "Titwala", "07:07", "08:53", TrainSpeedType.FAST, ac = true, pf = "PF 4", stops = listOf(
                "Titwala" to "07:07", "Kalyan" to "07:21", "Dombivli" to "07:32", "Thane" to "08:05", "Kurla" to "08:26", "Dadar Central" to "08:33", "CSMT" to "08:53"
            )),
            crUp("95714", "K 38 AC", "Kalyan", "08:46", "09:59", TrainSpeedType.FAST, ac = true, pf = "PF 5", stops = listOf(
                "Kalyan" to "08:46", "Dombivli" to "08:55", "Thane" to "09:19", "Ghatkopar" to "09:36", "Kurla" to "09:40", "Dadar Central" to "09:48", "CSMT" to "09:59"
            )),
            crUp("97048", "K 52 AC", "Kalyan", "10:25", "11:54", TrainSpeedType.FAST, ac = true, pf = "PF 4", stops = listOf(
                "Kalyan" to "10:25", "Dombivli" to "10:34", "Thane" to "10:58", "Ghatkopar" to "11:18", "Kurla" to "11:24", "Dadar Central" to "11:35", "CSMT" to "11:54"
            )),
            crUp("95210", "BL 20 AC", "Badlapur", "10:42", "12:12", TrainSpeedType.FAST, ac = true, pf = "PF 3", stops = listOf(
                "Badlapur" to "10:42", "Ambernath" to "10:51", "Kalyan" to "11:05", "Dombivli" to "11:12", "Thane" to "11:29", "Kurla" to "11:47", "Dadar Central" to "11:55", "CSMT" to "12:12"
            )),
            crUp("96518", "AN 28", "Asangaon", "19:42", "22:11", TrainSpeedType.FAST, pf = "PF 3", stops = listOf(
                "Asangaon" to "19:42", "Titwala" to "20:04", "Kalyan" to "20:21", "Dombivli" to "20:30", "Thane" to "20:53", "Kurla" to "21:21", "Dadar Central" to "21:32", "CSMT" to "22:11"
            )),
            crUp("96018", "SKP 18", "Khopoli", "21:09", "00:02", TrainSpeedType.FAST, pf = "PF 4", stops = listOf(
                "Khopoli" to "21:09", "Karjat" to "21:34", "Badlapur" to "22:15", "Kalyan" to "22:34", "Thane" to "23:07", "Dadar Central" to "23:46", "CSMT" to "00:02"
            ))
        ))

        // =========================================================================
        // 3. HARBOUR LINE (CSMT <-> Panvel & CSMT <-> Bandra/Goregaon)
        // =========================================================================

        // --- HARBOUR DOWN ---
        fun hbrDown(num: String, code: String, dest: String, destCode: String, dep: String, arr: String, speed: TrainSpeedType = TrainSpeedType.SLOW, ac: Boolean = false, pf: String = "PF 1", stops: List<Pair<String, String>>) =
            TrainSchedule(
                trainNumber = num, trainCode = code, line = "Harbour", direction = TrainDirection.DOWN,
                originStation = "CSMT", destinationStation = dest, destinationCode = destCode,
                departureTime = dep, arrivalTime = arr, speedType = speed, isAC = ac,
                platform = pf,
                stoppages = stops.map { StationStopTime(it.first, it.first.take(3).uppercase(), it.second, pf) }
            )

        list.addAll(listOf(
            hbrDown("98801", "B 1", "Bandra", "BA", "00:17", "00:45", stops = listOf(
                "CSMT" to "00:17", "Sandhurst Road" to "00:23", "Wadala Road" to "00:35", "Mahim Junction" to "00:41", "Bandra" to "00:45"
            )),
            hbrDown("98001", "PL 1", "Panvel", "PNVL", "00:13", "01:33", stops = listOf(
                "CSMT" to "00:13", "Wadala Road" to "00:31", "Kurla" to "00:42", "Vashi" to "01:02", "Nerul" to "01:11", "CBD Belapur" to "01:18", "Panvel" to "01:33"
            )),
            hbrDown("98701", "GN 1", "Goregaon", "GMN", "04:28", "05:22", stops = listOf(
                "CSMT" to "04:28", "Wadala Road" to "04:46", "Bandra" to "04:57", "Andheri" to "05:11", "Goregaon" to "05:22"
            )),
            hbrDown("98007", "PL 7", "Panvel", "PNVL", "04:32", "06:04", stops = listOf(
                "CSMT" to "04:32", "Wadala Road" to "04:50", "Kurla" to "05:01", "Vashi" to "05:21", "Nerul" to "05:30", "CBD Belapur" to "05:37", "Panvel" to "06:04"
            )),
            hbrDown("98045", "PL 39 AC", "Panvel", "PNVL", "07:17", "08:37", ac = true, stops = listOf(
                "CSMT" to "07:17", "Wadala Road" to "07:35", "Kurla" to "07:46", "Vashi" to "08:06", "Nerul" to "08:15", "CBD Belapur" to "08:22", "Panvel" to "08:37"
            )),
            hbrDown("98715", "GN 15", "Goregaon", "GMN", "07:05", "08:01", stops = listOf(
                "CSMT" to "07:05", "Wadala Road" to "07:23", "Bandra" to "07:36", "Andheri" to "07:50", "Goregaon" to "08:01"
            )),
            hbrDown("98051", "PL 43 AC", "Panvel", "PNVL", "07:40", "09:00", ac = true, stops = listOf(
                "CSMT" to "07:40", "Wadala Road" to "07:58", "Kurla" to "08:09", "Vashi" to "08:29", "Nerul" to "08:38", "CBD Belapur" to "08:45", "Panvel" to "09:00"
            )),
            hbrDown("98719", "GN 19", "Goregaon", "GMN", "08:04", "08:58", stops = listOf(
                "CSMT" to "08:04", "Wadala Road" to "08:22", "Bandra" to "08:33", "Andheri" to "08:47", "Goregaon" to "08:58"
            )),
            hbrDown("98083", "PL 69 AC", "Panvel", "PNVL", "10:10", "11:30", ac = true, stops = listOf(
                "CSMT" to "10:10", "Wadala Road" to "10:28", "Kurla" to "10:39", "Vashi" to "10:59", "Nerul" to "11:08", "CBD Belapur" to "11:15", "Panvel" to "11:30"
            )),
            hbrDown("98761", "GN 61", "Goregaon", "GMN", "18:05", "19:00", stops = listOf(
                "CSMT" to "18:05", "Wadala Road" to "18:23", "Bandra" to "18:34", "Andheri" to "18:49", "Goregaon" to "19:00"
            )),
            hbrDown("98201", "PL 161 AC", "Panvel", "PNVL", "18:33", "19:54", ac = true, stops = listOf(
                "CSMT" to "18:33", "Wadala Road" to "18:51", "Kurla" to "19:03", "Vashi" to "19:23", "Nerul" to "19:32", "CBD Belapur" to "19:39", "Panvel" to "19:54"
            )),
            hbrDown("98773", "GN 73", "Goregaon", "GMN", "20:36", "21:32", stops = listOf(
                "CSMT" to "20:36", "Wadala Road" to "20:54", "Bandra" to "21:05", "Andheri" to "21:20", "Goregaon" to "21:32"
            )),
            hbrDown("98239", "PL 187 AC", "Panvel", "PNVL", "21:30", "22:50", ac = true, stops = listOf(
                "CSMT" to "21:30", "Wadala Road" to "21:48", "Kurla" to "21:59", "Vashi" to "22:19", "Nerul" to "22:28", "CBD Belapur" to "22:35", "Panvel" to "22:50"
            ))
        ))

        // --- HARBOUR UP ---
        fun hbrUp(num: String, code: String, orig: String, dep: String, arr: String, speed: TrainSpeedType = TrainSpeedType.SLOW, ac: Boolean = false, pf: String = "PF 1", stops: List<Pair<String, String>>) =
            TrainSchedule(
                trainNumber = num, trainCode = code, line = "Harbour", direction = TrainDirection.UP,
                originStation = orig, destinationStation = "CSMT", destinationCode = "CSMT",
                departureTime = dep, arrivalTime = arr, speedType = speed, isAC = ac,
                platform = pf,
                stoppages = stops.map { StationStopTime(it.first, it.first.take(3).uppercase(), it.second, pf) }
            )

        list.addAll(listOf(
            hbrUp("98702", "GN 2", "Goregaon", "00:07", "01:02", stops = listOf(
                "Goregaon" to "00:07", "Andheri" to "00:18", "Bandra" to "00:32", "Wadala Road" to "00:44", "CSMT" to "01:02"
            )),
            hbrUp("98004", "PL 4", "Panvel", "04:03", "05:22", stops = listOf(
                "Panvel" to "04:03", "CBD Belapur" to "04:18", "Nerul" to "04:25", "Vashi" to "04:33", "Kurla" to "04:52", "Wadala Road" to "05:04", "CSMT" to "05:22"
            )),
            hbrUp("98708", "GN 8", "Goregaon", "05:33", "06:28", stops = listOf(
                "Goregaon" to "05:33", "Andheri" to "05:44", "Bandra" to "05:58", "Wadala Road" to "06:10", "CSMT" to "06:28"
            )),
            hbrUp("98020", "PL 20 AC", "Panvel", "06:17", "07:36", ac = true, stops = listOf(
                "Panvel" to "06:17", "CBD Belapur" to "06:32", "Nerul" to "06:39", "Vashi" to "06:47", "Kurla" to "07:06", "Wadala Road" to "07:18", "CSMT" to "07:36"
            )),
            hbrUp("98718", "GN 18", "Goregaon", "07:17", "08:12", stops = listOf(
                "Goregaon" to "07:17", "Andheri" to "07:28", "Bandra" to "07:42", "Wadala Road" to "07:54", "CSMT" to "08:12"
            )),
            hbrUp("98052", "PL 42 AC", "Panvel", "08:45", "10:06", ac = true, stops = listOf(
                "Panvel" to "08:45", "CBD Belapur" to "09:00", "Nerul" to "09:07", "Vashi" to "09:16", "Kurla" to "09:36", "Wadala Road" to "09:48", "CSMT" to "10:06"
            )),
            hbrUp("98728", "GN 28", "Goregaon", "09:46", "10:41", stops = listOf(
                "Goregaon" to "09:46", "Andheri" to "09:57", "Bandra" to "10:11", "Wadala Road" to "10:23", "CSMT" to "10:41"
            )),
            hbrUp("98092", "PL 70 AC", "Panvel", "11:43", "13:02", ac = true, stops = listOf(
                "Panvel" to "11:43", "CBD Belapur" to "11:58", "Nerul" to "12:05", "Vashi" to "12:13", "Kurla" to "12:32", "Wadala Road" to "12:44", "CSMT" to "13:02"
            )),
            hbrUp("98762", "GN 62", "Goregaon", "17:53", "18:49", stops = listOf(
                "Goregaon" to "17:53", "Andheri" to "18:04", "Bandra" to "18:18", "Wadala Road" to "18:31", "CSMT" to "18:49"
            )),
            hbrUp("98166", "PL 132 AC", "Panvel", "17:07", "18:26", ac = true, stops = listOf(
                "Panvel" to "17:07", "CBD Belapur" to "17:22", "Nerul" to "17:29", "Vashi" to "17:37", "Kurla" to "17:56", "Wadala Road" to "18:08", "CSMT" to "18:26"
            )),
            hbrUp("98204", "PL 162 AC", "Panvel", "20:07", "21:26", ac = true, stops = listOf(
                "Panvel" to "20:07", "CBD Belapur" to "20:22", "Nerul" to "20:29", "Vashi" to "20:37", "Kurla" to "20:56", "Wadala Road" to "21:08", "CSMT" to "21:26"
            )),
            hbrUp("98788", "GN 88", "Goregaon", "23:33", "00:28", stops = listOf(
                "Goregaon" to "23:33", "Andheri" to "23:44", "Bandra" to "23:58", "Wadala Road" to "00:10", "CSMT" to "00:28"
            ))
        ))

        // =========================================================================
        // 4. TRANS-HARBOUR LINE (Thane <-> Vashi / Panvel)
        // =========================================================================

        fun trDown(num: String, code: String, dest: String, destCode: String, dep: String, arr: String, pf: String = "PF 9", stops: List<Pair<String, String>>) =
            TrainSchedule(
                trainNumber = num, trainCode = code, line = "Trans-Harbour", direction = TrainDirection.DOWN,
                originStation = "Thane", destinationStation = dest, destinationCode = destCode,
                departureTime = dep, arrivalTime = arr, speedType = TrainSpeedType.SLOW,
                platform = pf,
                stoppages = stops.map { StationStopTime(it.first, it.first.take(3).uppercase(), it.second, pf) }
            )

        list.addAll(listOf(
            trDown("99001", "TPL 1", "Panvel", "PNVL", "00:05", "00:57", stops = listOf(
                "Thane" to "00:05", "Airoli" to "00:13", "Ghansoli" to "00:19", "Koparkhairane" to "00:22", "Turbhe" to "00:26", "Juinagar" to "00:30", "Nerul" to "00:34", "CBD Belapur" to "00:42", "Panvel" to "00:57"
            )),
            trDown("99401", "TV 1", "Vashi", "VSH", "05:25", "05:54", stops = listOf(
                "Thane" to "05:25", "Airoli" to "05:33", "Ghansoli" to "05:39", "Koparkhairane" to "05:42", "Turbhe" to "05:46", "Sanpada" to "05:50", "Vashi" to "05:54"
            )),
            trDown("99005", "TPL 5", "Panvel", "PNVL", "06:20", "07:12", stops = listOf(
                "Thane" to "06:20", "Airoli" to "06:28", "Turbhe" to "06:41", "Nerul" to "06:49", "CBD Belapur" to "06:57", "Kharghar" to "07:01", "Panvel" to "07:12"
            )),
            trDown("99413", "TV 13", "Vashi", "VSH", "07:35", "08:04", stops = listOf(
                "Thane" to "07:35", "Airoli" to "07:43", "Turbhe" to "07:56", "Sanpada" to "08:00", "Vashi" to "08:04"
            )),
            trDown("99011", "TPL 11", "Panvel", "PNVL", "08:41", "09:33", stops = listOf(
                "Thane" to "08:41", "Airoli" to "08:49", "Turbhe" to "09:02", "Nerul" to "09:10", "CBD Belapur" to "09:18", "Panvel" to "09:33"
            )),
            trDown("99427", "TV 27", "Vashi", "VSH", "09:30", "09:59", stops = listOf(
                "Thane" to "09:30", "Airoli" to "09:38", "Turbhe" to "09:51", "Sanpada" to "09:55", "Vashi" to "09:59"
            )),
            trDown("99023", "TPL 23", "Panvel", "PNVL", "12:00", "12:56", stops = listOf(
                "Thane" to "12:00", "Airoli" to "12:09", "Turbhe" to "12:23", "Nerul" to "12:33", "CBD Belapur" to "12:41", "Panvel" to "12:56"
            )),
            trDown("99471", "TV 71", "Vashi", "VSH", "16:07", "16:36", stops = listOf(
                "Thane" to "16:07", "Airoli" to "16:15", "Turbhe" to "16:28", "Sanpada" to "16:32", "Vashi" to "16:36"
            )),
            trDown("99041", "TPL 41", "Panvel", "PNVL", "16:48", "17:40", stops = listOf(
                "Thane" to "16:48", "Airoli" to "16:56", "Turbhe" to "17:09", "Nerul" to "17:17", "CBD Belapur" to "17:25", "Panvel" to "17:40"
            )),
            trDown("99491", "TV 91", "Vashi", "VSH", "18:59", "19:28", stops = listOf(
                "Thane" to "18:59", "Airoli" to "19:07", "Turbhe" to "19:20", "Sanpada" to "19:24", "Vashi" to "19:28"
            )),
            trDown("99069", "TPL 69", "Panvel", "PNVL", "22:55", "23:47", stops = listOf(
                "Thane" to "22:55", "Airoli" to "23:03", "Turbhe" to "23:16", "Nerul" to "23:24", "CBD Belapur" to "23:32", "Panvel" to "23:47"
            ))
        ))

        fun trUp(num: String, code: String, orig: String, dep: String, arr: String, pf: String = "PF 1", stops: List<Pair<String, String>>) =
            TrainSchedule(
                trainNumber = num, trainCode = code, line = "Trans-Harbour", direction = TrainDirection.UP,
                originStation = orig, destinationStation = "Thane", destinationCode = "TNA",
                departureTime = dep, arrivalTime = arr, speedType = TrainSpeedType.SLOW,
                platform = pf,
                stoppages = stops.map { StationStopTime(it.first, it.first.take(3).uppercase(), it.second, pf) }
            )

        list.addAll(listOf(
            trUp("99002", "TPL 2", "Panvel", "04:33", "05:25", stops = listOf(
                "Panvel" to "04:33", "CBD Belapur" to "04:48", "Nerul" to "04:56", "Turbhe" to "05:04", "Airoli" to "05:17", "Thane" to "05:25"
            )),
            trUp("99402", "TV 2", "Vashi", "05:29", "05:58", stops = listOf(
                "Vashi" to "05:29", "Sanpada" to "05:33", "Turbhe" to "05:37", "Airoli" to "05:50", "Thane" to "05:58"
            )),
            trUp("99010", "TPL 10", "Panvel", "06:13", "07:05", stops = listOf(
                "Panvel" to "06:13", "CBD Belapur" to "06:28", "Nerul" to "06:36", "Turbhe" to "06:44", "Airoli" to "06:57", "Thane" to "07:05"
            )),
            trUp("99020", "TPL 20", "Panvel", "08:04", "08:56", stops = listOf(
                "Panvel" to "08:04", "CBD Belapur" to "08:19", "Nerul" to "08:27", "Turbhe" to "08:35", "Airoli" to "08:48", "Thane" to "08:56"
            )),
            trUp("99426", "TV 26", "Vashi", "09:18", "09:47", stops = listOf(
                "Vashi" to "09:18", "Sanpada" to "09:22", "Turbhe" to "09:26", "Airoli" to "09:39", "Thane" to "09:47"
            )),
            trUp("99036", "TPL 36", "Panvel", "13:04", "13:56", stops = listOf(
                "Panvel" to "13:04", "CBD Belapur" to "13:19", "Nerul" to "13:27", "Turbhe" to "13:35", "Airoli" to "13:48", "Thane" to "13:56"
            )),
            trUp("99046", "TPL 46", "Panvel", "15:53", "16:45", stops = listOf(
                "Panvel" to "15:53", "CBD Belapur" to "16:08", "Nerul" to "16:16", "Turbhe" to "16:24", "Airoli" to "16:37", "Thane" to "16:45"
            )),
            trUp("99488", "TV 88", "Vashi", "18:25", "18:54", stops = listOf(
                "Vashi" to "18:25", "Sanpada" to "18:29", "Turbhe" to "18:33", "Airoli" to "18:46", "Thane" to "18:54"
            )),
            trUp("99066", "TPL 66", "Panvel", "20:19", "21:11", stops = listOf(
                "Panvel" to "20:19", "CBD Belapur" to "20:34", "Nerul" to "20:42", "Turbhe" to "20:50", "Airoli" to "21:03", "Thane" to "21:11"
            )),
            trUp("99074", "TPL 74", "Panvel", "23:18", "00:10", stops = listOf(
                "Panvel" to "23:18", "CBD Belapur" to "23:33", "Nerul" to "23:41", "Turbhe" to "23:49", "Airoli" to "00:02", "Thane" to "00:10"
            ))
        ))

        // =========================================================================
        // 5. URAN / PORT LINE (Nerul / Belapur <-> Uran) (w.e.f 15th Dec 2025)
        // =========================================================================

        fun uranDown(num: String, code: String, orig: String, dep: String, arr: String, pf: String = "PF 1", stops: List<Pair<String, String>>) =
            TrainSchedule(
                trainNumber = num, trainCode = code, line = "Uran", direction = TrainDirection.DOWN,
                originStation = orig, destinationStation = "Uran City", destinationCode = "URAN",
                departureTime = dep, arrivalTime = arr, speedType = TrainSpeedType.SLOW,
                platform = pf,
                stoppages = stops.map { StationStopTime(it.first, it.first.take(3).uppercase(), it.second, pf) }
            )

        list.addAll(listOf(
            uranDown("99701", "UBR 1", "CBD Belapur", "05:30", "06:03", stops = listOf(
                "CBD Belapur" to "05:30", "Bamandongri" to "05:42", "Kharkopar" to "05:46", "Dronagiri" to "06:00", "Uran City" to "06:03"
            )),
            uranDown("99601", "UNU 1", "Nerul", "06:15", "06:49", stops = listOf(
                "Nerul" to "06:15", "Seawoods-Darave" to "06:19", "Bamandongri" to "06:28", "Kharkopar" to "06:32", "Dronagiri" to "06:46", "Uran City" to "06:49"
            )),
            uranDown("99703", "UBR 3", "CBD Belapur", "06:45", "07:18", stops = listOf(
                "CBD Belapur" to "06:45", "Bamandongri" to "06:57", "Kharkopar" to "07:01", "Dronagiri" to "07:15", "Uran City" to "07:18"
            )),
            uranDown("99603", "UNU 3", "Nerul", "07:15", "07:49", stops = listOf(
                "Nerul" to "07:15", "Seawoods-Darave" to "07:19", "Bamandongri" to "07:28", "Kharkopar" to "07:32", "Dronagiri" to "07:46", "Uran City" to "07:49"
            )),
            uranDown("99705", "UBR 5", "CBD Belapur", "07:45", "08:18", stops = listOf(
                "CBD Belapur" to "07:45", "Bamandongri" to "07:57", "Kharkopar" to "08:01", "Dronagiri" to "08:15", "Uran City" to "08:18"
            )),
            uranDown("99605", "UNU 5", "Nerul", "08:15", "08:49", stops = listOf(
                "Nerul" to "08:15", "Seawoods-Darave" to "08:19", "Bamandongri" to "08:28", "Kharkopar" to "08:32", "Dronagiri" to "08:46", "Uran City" to "08:49"
            )),
            uranDown("99707", "UBR 7", "CBD Belapur", "08:45", "09:18", stops = listOf(
                "CBD Belapur" to "08:45", "Bamandongri" to "08:57", "Kharkopar" to "09:01", "Dronagiri" to "09:15", "Uran City" to "09:18"
            )),
            uranDown("99607", "UNU 7", "Nerul", "09:15", "09:49", stops = listOf(
                "Nerul" to "09:15", "Seawoods-Darave" to "09:19", "Bamandongri" to "09:28", "Kharkopar" to "09:32", "Dronagiri" to "09:46", "Uran City" to "09:49"
            )),
            uranDown("99719", "UBR 19", "CBD Belapur", "18:30", "19:03", stops = listOf(
                "CBD Belapur" to "18:30", "Bamandongri" to "18:42", "Kharkopar" to "18:46", "Dronagiri" to "19:00", "Uran City" to "19:03"
            )),
            uranDown("99621", "UNU 21", "Nerul", "20:00", "20:34", stops = listOf(
                "Nerul" to "20:00", "Seawoods-Darave" to "20:04", "Bamandongri" to "20:13", "Kharkopar" to "20:17", "Dronagiri" to "20:31", "Uran City" to "20:34"
            )),
            uranDown("99725", "UBR 25", "CBD Belapur", "22:30", "23:03", stops = listOf(
                "CBD Belapur" to "22:30", "Bamandongri" to "22:42", "Kharkopar" to "22:46", "Dronagiri" to "23:00", "Uran City" to "23:03"
            ))
        ))

        fun uranUp(num: String, code: String, dest: String, dep: String, arr: String, pf: String = "PF 1", stops: List<Pair<String, String>>) =
            TrainSchedule(
                trainNumber = num, trainCode = code, line = "Uran", direction = TrainDirection.UP,
                originStation = "Uran City", destinationStation = dest, destinationCode = dest.take(3).uppercase(),
                departureTime = dep, arrivalTime = arr, speedType = TrainSpeedType.SLOW,
                platform = pf,
                stoppages = stops.map { StationStopTime(it.first, it.first.take(3).uppercase(), it.second, pf) }
            )

        list.addAll(listOf(
            uranUp("99702", "UBR 2", "CBD Belapur", "05:30", "06:03", stops = listOf(
                "Uran City" to "05:30", "Dronagiri" to "05:32", "Kharkopar" to "05:46", "Bamandongri" to "05:50", "CBD Belapur" to "06:03"
            )),
            uranUp("99602", "UNU 2", "Nerul", "06:10", "06:44", stops = listOf(
                "Uran City" to "06:10", "Dronagiri" to "06:12", "Kharkopar" to "06:26", "Bamandongri" to "06:30", "Seawoods-Darave" to "06:38", "Nerul" to "06:44"
            )),
            uranUp("99704", "UBR 4", "CBD Belapur", "06:40", "07:13", stops = listOf(
                "Uran City" to "06:40", "Dronagiri" to "06:42", "Kharkopar" to "06:56", "Bamandongri" to "07:00", "CBD Belapur" to "07:13"
            )),
            uranUp("99604", "UNU 4", "Nerul", "07:15", "07:49", stops = listOf(
                "Uran City" to "07:15", "Dronagiri" to "07:17", "Kharkopar" to "07:31", "Bamandongri" to "07:35", "Seawoods-Darave" to "07:43", "Nerul" to "07:49"
            )),
            uranUp("99706", "UBR 6", "CBD Belapur", "07:45", "08:18", stops = listOf(
                "Uran City" to "07:45", "Dronagiri" to "07:47", "Kharkopar" to "08:01", "Bamandongri" to "08:05", "CBD Belapur" to "08:18"
            )),
            uranUp("99606", "UNU 6", "Nerul", "08:15", "08:49", stops = listOf(
                "Uran City" to "08:15", "Dronagiri" to "08:17", "Kharkopar" to "08:31", "Bamandongri" to "08:35", "Seawoods-Darave" to "08:43", "Nerul" to "08:49"
            )),
            uranUp("99708", "UBR 8", "CBD Belapur", "08:45", "09:18", stops = listOf(
                "Uran City" to "08:45", "Dronagiri" to "08:47", "Kharkopar" to "09:01", "Bamandongri" to "09:05", "CBD Belapur" to "09:18"
            )),
            uranUp("99618", "UNU 18", "Nerul", "18:00", "18:34", stops = listOf(
                "Uran City" to "18:00", "Dronagiri" to "18:02", "Kharkopar" to "18:16", "Bamandongri" to "18:20", "Seawoods-Darave" to "18:28", "Nerul" to "18:34"
            )),
            uranUp("99722", "UBR 22", "CBD Belapur", "20:05", "20:28", stops = listOf(
                "Uran City" to "20:05", "Dronagiri" to "20:07", "Kharkopar" to "20:21", "Bamandongri" to "20:25", "CBD Belapur" to "20:28"
            )),
            uranUp("99726", "UBR 26", "CBD Belapur", "22:30", "23:03", stops = listOf(
                "Uran City" to "22:30", "Dronagiri" to "22:32", "Kharkopar" to "22:46", "Bamandongri" to "22:50", "CBD Belapur" to "23:03"
            ))
        ))

        return list
    }

    /**
     * Get schedules with filters like M-Indicator
     */
    fun getFilteredSchedules(
        line: String,
        direction: TrainDirection,
        sourceStation: String? = null,
        destinationStation: String? = null,
        fastOnly: Boolean = false,
        slowOnly: Boolean = false,
        acOnly: Boolean = false,
        car15Only: Boolean = false,
        ladiesOnly: Boolean = false
    ): List<TrainSchedule> {
        return allSchedules.filter { sched ->
            sched.line.equals(line, ignoreCase = true) &&
            sched.direction == direction &&
            (!fastOnly || sched.speedType == TrainSpeedType.FAST) &&
            (!slowOnly || sched.speedType == TrainSpeedType.SLOW) &&
            (!acOnly || sched.isAC) &&
            (!car15Only || sched.is15Car) &&
            (!ladiesOnly || sched.isLadiesSpecial) &&
            (sourceStation == null || sched.stoppages.any { it.stationName.contains(sourceStation, ignoreCase = true) } || sched.originStation.contains(sourceStation, ignoreCase = true)) &&
            (destinationStation == null || sched.stoppages.any { it.stationName.contains(destinationStation, ignoreCase = true) } || sched.destinationStation.contains(destinationStation, ignoreCase = true))
        }.sortedBy { it.departureTime }
    }

    /**
     * Get next upcoming trains from a specific station
     */
    fun getUpcomingTrainsForStation(
        stationName: String,
        line: String,
        direction: TrainDirection,
        limit: Int = 10
    ): List<Pair<TrainSchedule, String>> {
        val currentMinutes = getCurrentTimeInMinutes()
        val trainsAtStation = mutableListOf<Pair<TrainSchedule, String>>()

        val matching = allSchedules.filter {
            it.line.equals(line, ignoreCase = true) && it.direction == direction
        }

        for (train in matching) {
            val stop = train.stoppages.firstOrNull { it.stationName.contains(stationName, ignoreCase = true) }
            val timeStr = stop?.time ?: if (train.originStation.contains(stationName, ignoreCase = true)) train.departureTime else null

            if (timeStr != null) {
                trainsAtStation.add(train to timeStr)
            }
        }

        // Sort relative to current time (trains leaving soonest first)
        return trainsAtStation.sortedBy { (_, timeStr) ->
            val trainMins = parseTimeToMinutes(timeStr)
            if (trainMins >= currentMinutes) trainMins - currentMinutes else (trainMins + 1440) - currentMinutes
        }.take(limit)
    }

    fun parseTimeToMinutes(timeStr: String): Int {
        return try {
            val parts = timeStr.trim().split(":")
            val h = parts[0].toInt()
            val m = parts[1].toInt()
            h * 60 + m
        } catch (e: Exception) {
            0
        }
    }

    fun getCurrentTimeInMinutes(): Int {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    }

    fun formatMinutesUntil(trainTimeStr: String): String {
        val currentMins = getCurrentTimeInMinutes()
        val trainMins = parseTimeToMinutes(trainTimeStr)
        val diff = if (trainMins >= currentMins) trainMins - currentMins else (trainMins + 1440) - currentMins

        return when {
            diff <= 1 -> "Due now"
            diff in 2..59 -> "in ${diff}m"
            else -> "in ${diff / 60}h ${diff % 60}m"
        }
    }
}
