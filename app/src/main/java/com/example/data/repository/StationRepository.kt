package com.example.data.repository

import com.example.data.model.Station
import com.example.data.model.TrainDirection
import kotlin.math.*

object StationRepository {

    val stations: List<Station> = listOf(
        // Western Line (South to North)
        Station(1, "Churchgate", "CCG", "Western", 18.93548, 72.82717, 0.0, 0, 4, "Marine Drive, Nariman Point"),
        Station(2, "Marine Lines", "MEL", "Western", 18.94576, 72.82372, 1.0, 3, 4, "Marine Drive, Girgaum Chowpatty"),
        Station(3, "Charni Road", "CYR", "Western", 18.95184, 72.81824, 1.0, 2, 4, "Girgaum Chowpatty, Taraporewala Aquarium"),
        Station(4, "Grant Road", "GTR", "Western", 18.96336, 72.81583, 1.0, 3, 4, "Lamington Road, Nana Chowk"),
        Station(5, "Mumbai Central", "MMCT", "Western", 18.96970, 72.81940, 1.0, 2, 9, "Haji Ali Dargah, Mahalakshmi Temple"),
        Station(6, "Mahalaxmi", "MX", "Western", 18.98220, 72.82380, 1.0, 3, 3, "Racecourse, Dhobi Ghat"),
        Station(7, "Lower Parel", "PL", "Western", 18.99880, 72.82580, 2.0, 3, 3, "High Street Phoenix, Kamala Mills"),
        Station(8, "Prabhadevi", "PBHD", "Western", 19.00809, 72.83653, 1.0, 3, 2, "Siddhivinayak Temple, Shivaji Park"),
        Station(9, "Dadar Western", "DDR", "Western", 19.01834, 72.84321, 1.0, 2, 7, "Shivaji Park, Dadar Flower Market"),
        Station(10, "Matunga Road", "MRU", "Western", 19.02700, 72.85000, 2.0, 2, 2, "Ruparel College, Shivaji Park"),
        Station(11, "Mahim Junction", "MM", "Western", 19.04130, 72.84000, 1.0, 3, 6, "Bandra-Worli Sea Link, Mahim Dargah"),
        Station(12, "Bandra", "BA", "Western", 19.05444, 72.84056, 2.0, 4, 7, "Bandstand, Mount Mary, Carter Road"),
        Station(13, "Khar Road", "KHAR", "Western", 19.06960, 72.83970, 2.0, 3, 6, "Khar Danda, Bandra Terminus"),
        Station(14, "Santacruz", "STC", "Western", 19.08160, 72.84100, 1.0, 2, 6, "Juhu Beach, Vakola"),
        Station(15, "Vile Parle", "VLP", "Western", 19.10000, 72.84300, 2.0, 3, 6, "Juhu Beach, NMIMS College"),
        Station(16, "Andheri", "ADH", "Western", 19.11970, 72.84642, 2.0, 5, 9, "Versova Beach, Line 1 Metro Interchange"),
        Station(17, "Jogeshwari", "JOS", "Western", 19.13640, 72.84980, 2.0, 3, 6, "Jogeshwari Caves, Mahakali"),
        Station(18, "Ram Mandir", "RMAR", "Western", 19.15450, 72.85050, 2.0, 3, 4, "Oshiwara, Ram Mandir"),
        Station(19, "Goregaon", "GMN", "Western", 19.16630, 72.84990, 3.0, 2, 7, "Film City, Oberoi Mall"),
        Station(20, "Malad", "MDD", "Western", 19.18640, 72.84800, 2.0, 4, 4, "Inorbit Mall, Aksa Beach, Marve"),
        Station(21, "Kandivali", "KLE", "Western", 19.20410, 72.85120, 1.0, 3, 4, "Sanjay Gandhi National Park"),
        Station(22, "Borivali", "BVI", "Western", 19.22907, 72.85736, 3.0, 6, 10, "Kanheri Caves, Gorai Beach, SGNP"),
        Station(23, "Dahisar", "DIC", "Western", 19.25400, 72.85930, 2.0, 5, 4, "Mandapeshwar Caves"),
        Station(24, "Mira Road", "MIRA", "Western", 19.27990, 72.85610, 3.0, 5, 4, "Global Vipassana Pagoda, EsselWorld"),
        Station(25, "Bhayandar", "BY", "Western", 19.30180, 72.85100, 3.0, 5, 6, "Bhayandar Creek, Uttan Beach"),
        Station(26, "Naigaon", "NIG", "Western", 19.35140, 72.84650, 6.0, 6, 3, "Tungareshwar Sanctuary"),
        Station(27, "Vasai Road", "BSR", "Western", 19.38267, 72.83203, 4.0, 5, 8, "Vasai Fort, Suruchi Beach"),
        Station(28, "Nalasopara", "NSP", "Western", 19.41750, 72.81800, 4.0, 5, 4, "Chakreshwar Temple, Nirmal Beach"),
        Station(29, "Virar", "VR", "Western", 19.45531, 72.81182, 4.0, 6, 8, "Jivdani Mata Temple, Arnala Fort"),
        Station(30, "Vaitarna", "VTN", "Western", 19.47400, 72.80000, 8.0, 9, 2, "Vaitarna River Bridge"),
        Station(31, "Saphale", "SAH", "Western", 19.57600, 72.82700, 8.0, 8, 2, "Kelva Beach"),
        Station(32, "Kelve Road", "KLV", "Western", 19.61000, 72.76000, 6.0, 8, 3, "Kelva Beach, Shirgaon Fort"),
        Station(33, "Palghar", "PLG", "Western", 19.69670, 72.76530, 8.0, 15, 3, "District HQ, Shirgaon Beach"),
        Station(34, "Umroli", "UOI", "Western", 19.75600, 72.76200, 6.0, 6, 2, "Palghar MIDC"),
        Station(35, "Boisar", "BOR", "Western", 19.80300, 72.75700, 6.0, 6, 3, "Tarapur Atomic Power Station"),
        Station(36, "Vangaon", "VGN", "Western", 19.88200, 72.75600, 9.0, 9, 3, "Vangaon Chikoo Orchards"),
        Station(37, "Dahanu Road", "DRD", "Western", 19.99000, 72.74400, 12.0, 13, 5, "Bordi Beach, Dahanu Fort"),

        // Central Main Line (South to North)
        Station(38, "CSMT", "CSMT", "Central", 18.93980, 72.83550, 0.0, 0, 18, "Gateway of India, UNESCO Heritage"),
        Station(39, "Masjid", "MSD", "Central", 18.95150, 72.83800, 1.0, 3, 4, "Crawford Market, Lokhand Bazaar"),
        Station(40, "Sandhurst Road", "SNRD", "Central", 18.96030, 72.83930, 1.0, 2, 4, "Dongri, Two-tier station"),
        Station(41, "Byculla", "BY", "Central", 18.97600, 72.83380, 2.0, 3, 4, "Rani Baug Zoo, Bhau Daji Lad Museum"),
        Station(42, "Chinchpokli", "CHG", "Central", 18.98570, 72.83250, 1.0, 2, 2, "Dhobi Ghat, Lalbaughcha Raja"),
        Station(43, "Currey Road", "CRD", "Central", 18.99480, 72.8326, 1.0, 2, 2, "Lalbaug, Lower Parel connector"),
        Station(44, "Parel", "PR", "Central", 19.00100, 72.83700, 2.0, 3, 3, "Prabhadevi FOB Interchange"),
        Station(45, "Dadar Central", "DR", "Central", 19.01834, 72.84321, 1.0, 3, 8, "Shivaji Park, Main Mega Junction"),
        Station(46, "Matunga", "MTN", "Central", 19.02750, 72.85500, 1.0, 3, 4, "Five Gardens, First all-women station"),
        Station(47, "Sion", "SIN", "Central", 19.04600, 72.86200, 1.0, 4, 4, "Sion Fort, Somaiya College"),
        Station(48, "Kurla", "C", "Central", 19.06560, 72.87910, 1.0, 4, 8, "Harbour Line Interchange, BKC"),
        Station(49, "Vidyavihar", "VVH", "Central", 19.08050, 72.89600, 2.0, 3, 2, "Somaiya Vidyavihar University"),
        Station(50, "Ghatkopar", "G", "Central", 19.08600, 72.90800, 2.0, 3, 4, "Line 1 Metro Interchange, R City Mall"),
        Station(51, "Vikhroli", "VK", "Central", 19.11100, 72.92700, 4.0, 5, 4, "Godrej Township, Powai Access"),
        Station(52, "Kanjur Marg", "KJMG", "Central", 19.13090, 72.93670, 2.0, 3, 3, "IIT Bombay, Hiranandani Powai"),
        Station(53, "Bhandup", "BND", "Central", 19.14500, 72.93700, 2.0, 3, 4, "Industrial Hub, Powai Lake"),
        Station(54, "Nahur", "NHU", "Central", 19.15090, 72.95190, 1.0, 3, 2, "Mulund-Goregaon Link Road"),
        Station(55, "Mulund", "MLND", "Central", 19.17240, 72.95650, 2.0, 3, 4, "Sanjay Gandhi National Park Border"),
        Station(56, "Thane", "TNA", "Central", 19.18600, 72.97500, 2.0, 4, 10, "Upvan Lake, Trans-Harbour Junction"),
        Station(57, "Kalwa", "KLVA", "Central", 19.19900, 72.99600, 2.0, 4, 4, "Parsik Hills, Creek Bridge"),
        Station(58, "Mumbra", "MBQ", "Central", 19.19100, 73.01900, 3.0, 6, 4, "Parsik Tunnel 1.3km"),
        Station(59, "Diva Junction", "DIVA", "Central", 19.19000, 73.04300, 3.0, 4, 8, "Vasai-Roha Corridor Junction"),
        Station(60, "Kopar", "KOPR", "Central", 19.21800, 73.08300, 4.0, 5, 2, "Two-level junction station"),
        Station(61, "Dombivli", "DI", "Central", 19.21830, 73.08600, 1.0, 3, 5, "Busiest suburban hub"),
        Station(62, "Thakurli", "THK", "Central", 19.22500, 73.10000, 1.0, 3, 2, "Thermal Power Plant Colony"),
        Station(63, "Kalyan Junction", "KYN", "Central", 19.23500, 73.13000, 3.0, 6, 8, "Kasara/Karjat Branching Junction"),

        // Kalyan -> Karjat / Khopoli branch
        Station(64, "Vithalwadi", "VLDI", "Central", 19.22800, 73.15100, 2.0, 5, 2, "Ulhasnagar Border"),
        Station(65, "Ulhasnagar", "ULNR", "Central", 19.21600, 73.16300, 1.0, 3, 2, "Commercial Market City"),
        Station(66, "Ambarnath", "ABH", "Central", 19.18600, 73.19100, 3.0, 6, 3, "Shiv Mandir 1060 CE UNESCO"),
        Station(67, "Badlapur", "BUD", "Central", 19.15500, 73.26500, 7.0, 8, 3, "Chikhloli Dam, Barvi Dam"),
        Station(68, "Vangani", "VGI", "Central", 19.11300, 73.28700, 11.0, 9, 2, "Bhivpuri Waterfall, Nakhind Hill"),
        Station(69, "Shelu", "SHLU", "Central", 19.07800, 73.29600, 4.0, 4, 2, "Engineering Colleges"),
        Station(70, "Neral Junction", "NRL", "Central", 19.02600, 73.31600, 4.0, 4, 2, "Matheran Toy Train Origin"),
        Station(71, "Bhivpuri Road", "BVS", "Central", 18.98900, 73.32200, 7.0, 7, 2, "Bhivpuri Hydro Power Plant"),
        Station(72, "Karjat", "KJT", "Central", 18.91000, 73.32300, 7.0, 9, 5, "Famous Vada Pav, Pune line connector"),
        Station(73, "Palasdhari", "PDI", "Central", 18.88200, 73.32600, 3.0, 5, 1, "Palasdari Lake"),
        Station(74, "Khopoli", "KP", "Central", 18.78500, 73.32900, 12.0, 17, 2, "Imagicaa Theme Park, Zenith Falls"),

        // Kalyan -> Kasara branch
        Station(75, "Shahad", "SHAD", "Central", 19.24900, 73.15400, 3.0, 5, 2, "Century Rayon, Birla Temple"),
        Station(76, "Ambivli", "ABY", "Central", 19.26900, 73.16500, 2.0, 3, 2, "Mohone Township"),
        Station(77, "Titwala", "TLA", "Central", 19.29500, 73.20900, 6.0, 5, 3, "Mahaganapati Mandir"),
        Station(78, "Khadavli", "KDV", "Central", 19.30300, 73.24300, 7.0, 8, 2, "Kalu River, Bhatsa River"),
        Station(79, "Vasind", "VSD", "Central", 19.41000, 73.28100, 8.0, 8, 2, "JSW Steel, Mahuli Fort"),
        Station(80, "Asangaon", "ASO", "Central", 19.44200, 73.30600, 6.0, 6, 2, "Manas Mandir, Shahapur"),
        Station(81, "Atgaon", "ATG", "Central", 19.48400, 73.35400, 9.0, 9, 2, "Tansa Lake Watershed"),
        Station(82, "Khardi", "KE", "Central", 19.54200, 73.45400, 12.0, 12, 2, "Middle Vaitarna Dam"),
        Station(83, "Kasara", "KSRA", "Central", 19.63300, 73.48300, 18.0, 18, 4, "Thal Ghat, Banker Locomotive attached"),

        // Harbour Line (CSMT -> Panvel)
        Station(84, "Dockyard Road", "DKRD", "Harbour", 18.96600, 72.84400, 2.0, 2, 2, "Mazagon Dock, Bhaucha Dhakka"),
        Station(85, "Reay Road", "RRD", "Harbour", 18.97800, 72.84400, 1.0, 2, 2, "Grade-I Heritage, GIPR original"),
        Station(86, "Cotton Green", "CTGN", "Harbour", 18.98600, 72.85100, 1.0, 2, 2, "Kalachowki, Cotton Exchange"),
        Station(87, "Sewri", "SVE", "Harbour", 18.99800, 72.85400, 1.0, 3, 2, "Sewri Flamingo Sanctuary, MTHL"),
        Station(88, "Wadala Road", "VDLR", "Harbour", 19.01700, 72.85800, 2.0, 3, 4, "Bandra Branching Junction"),
        Station(89, "GTB Nagar", "GTBN", "Harbour", 19.03500, 72.86300, 2.0, 4, 2, "Sion-Koliwada"),
        Station(90, "Chunabhatti", "CHF", "Harbour", 19.05100, 72.87300, 2.0, 3, 2, "BKC Connector Flyover"),
        Station(91, "Tilak Nagar", "TKNG", "Harbour", 19.06600, 72.89000, 2.0, 3, 2, "Lokmanya Tilak Terminus LTT"),
        Station(92, "Chembur", "CMBR", "Harbour", 19.06200, 72.89700, 1.0, 2, 2, "Mumbai Monorail Interchange"),
        Station(93, "Govandi", "GV", "Harbour", 19.05500, 72.91400, 3.0, 3, 2, "Deonar, Chembur East"),
        Station(94, "Mankhurd", "MNKD", "Harbour", 19.04810, 72.93170, 3.0, 3, 2, "BARC, Vashi Creek Bridge entry"),
        Station(95, "Vashi", "VSH", "Harbour", 19.06320, 72.99880, 8.0, 8, 4, "Inorbit Mall, Gateway to Navi Mumbai"),
        Station(96, "Sanpada", "SNCR", "Harbour", 19.06000, 73.01000, 3.0, 3, 4, "Palm Beach Road, APMC Market"),
        Station(97, "Juinagar", "JNJ", "Harbour", 19.04600, 73.01700, 5.0, 5, 6, "Trans-Harbour & Uran Junction"),
        Station(98, "Nerul", "NEU", "Harbour", 19.03300, 73.01800, 3.0, 3, 6, "DY Patil Stadium, Rock Garden"),
        Station(99, "Seawoods-Darave", "SWDV", "Harbour", 19.02000, 73.01900, 3.0, 3, 4, "Grand Central Mall above station"),
        Station(100, "CBD Belapur", "BEPR", "Harbour", 19.01780, 73.04000, 4.0, 4, 4, "Navi Mumbai Commercial Hub"),
        Station(101, "Kharghar", "KHAG", "Harbour", 19.04700, 73.06900, 4.0, 4, 4, "Central Park, Golf Course, ISKCON"),
        Station(102, "Mansarovar", "MANR", "Harbour", 19.01900, 73.07600, 3.0, 3, 2, "Kamothe Node"),
        Station(103, "Khandeshwar", "KNDS", "Harbour", 19.00600, 73.09300, 3.0, 3, 2, "New Panvel West"),
        Station(104, "Panvel", "PNVL", "Harbour", 18.98900, 73.11700, 3.0, 3, 7, "Major Konkan Terminus, Airport"),

        // Trans-Harbour Line (Thane -> Panvel / Vashi)
        Station(105, "Digha Gaon", "DIGHA", "Trans-Harbour", 19.18400, 73.00600, 4.0, 5, 2, "Airoli Link Road"),
        Station(106, "Airoli", "AIRL", "Trans-Harbour", 19.15852, 72.99940, 4.0, 4, 2, "Mindspace IT Park, Mangroves"),
        Station(107, "Rabale", "RABE", "Trans-Harbour", 19.13670, 73.00280, 3.0, 3, 2, "Rabale MIDC IT Parks"),
        Station(108, "Ghansoli", "GNSL", "Trans-Harbour", 19.12600, 73.00600, 3.0, 3, 2, "Reliance Corporate Park RCP"),
        Station(109, "Koparkhairane", "KPHN", "Trans-Harbour", 19.10200, 73.00400, 3.0, 3, 2, "Dhirubhai Ambani Life Sciences"),
        Station(110, "Turbhe", "TUH", "Trans-Harbour", 19.07500, 73.01700, 4.0, 4, 4, "APMC Fruit/Grain Market"),

        // Uran Line (Nerul/Belapur -> Uran)
        Station(111, "Bamandongri", "BMNDG", "Uran", 18.96600, 73.02300, 9.0, 12, 2, "Ulwe Node Residential"),
        Station(112, "Kharkopar", "KARP", "Uran", 18.94800, 73.01800, 3.0, 4, 2, "Ulwe South, Ramsheth Thakur Stadium"),
        Station(113, "Shevale", "SHVL", "Uran", 18.92200, 73.00200, 4.0, 5, 2, "JNPT SEZ"),
        Station(114, "Dronagiri", "DRNG", "Uran", 18.89500, 72.97800, 4.0, 6, 2, "ONGC Uran, Dronagiri Fort"),
        Station(115, "Uran City", "URAN", "Uran", 18.88000, 72.93500, 4.0, 6, 2, "Mora Jetty, Elephanta Ferry")
    )

    fun calculateHaversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun findNearestStation(lat: Double, lon: Double): Station {
        return stations.minByOrNull { calculateHaversineDistanceKm(lat, lon, it.latitude, it.longitude) }
            ?: stations.first()
    }

    fun detectDirection(lat1: Double, lat2: Double, speedKmh: Double): TrainDirection {
        if (speedKmh < 5.0) return TrainDirection.STATIONARY
        val deltaLat = lat2 - lat1
        return when {
            deltaLat < -0.0005 -> TrainDirection.UP // Moving South towards Churchgate / CSMT
            deltaLat > 0.0005 -> TrainDirection.DOWN // Moving North towards Virar / Kasara / Panvel
            else -> TrainDirection.UNKNOWN
        }
    }

    fun getNextUpcomingStations(currentStation: Station, direction: TrainDirection, count: Int = 4): List<Station> {
        val lineStations = stations.filter { it.line == currentStation.line }
        val currentIndex = lineStations.indexOfFirst { it.name.equals(currentStation.name, ignoreCase = true) }
        if (currentIndex == -1) return lineStations.take(count)

        return if (direction == TrainDirection.UP) {
            // Heading towards 0 (South)
            val prev = (currentIndex - 1 downTo max(0, currentIndex - count)).map { lineStations[it] }
            if (prev.isEmpty()) lineStations.take(count) else prev
        } else {
            // Heading towards end (North)
            val next = (currentIndex + 1..min(lineStations.size - 1, currentIndex + count)).map { lineStations[it] }
            if (next.isEmpty()) lineStations.takeLast(count) else next
        }
    }

    fun searchStations(query: String): List<Station> {
        if (query.isBlank()) return minimalStations
        val clean = query.trim().lowercase()
        return stations.filter {
            it.name.lowercase().contains(clean) ||
            it.code.lowercase().contains(clean) ||
            it.line.lowercase().contains(clean)
        }
    }

    val minimalStations: List<Station> = listOf(
        // Western Line
        Station(1, "Churchgate", "CCG", "Western", 18.93548, 72.82717, 0.0, 0, 4, "Marine Drive"),
        Station(5, "Mumbai Central", "MMCT", "Western", 18.96970, 72.81940, 1.0, 2, 9, "Haji Ali"),
        Station(9, "Dadar Western", "DDR", "Western", 19.01834, 72.84321, 1.0, 2, 7, "Interchange Junction"),
        Station(12, "Bandra", "BA", "Western", 19.05444, 72.84056, 2.0, 4, 7, "Bandstand, BKC"),
        Station(16, "Andheri", "ADH", "Western", 19.11970, 72.84642, 2.0, 5, 9, "Metro Interchange"),
        Station(22, "Borivali", "BVI", "Western", 19.22907, 72.85736, 3.0, 6, 10, "National Park"),
        // Central Line
        Station(38, "CSMT", "CSMT", "Central", 18.93980, 72.83550, 0.0, 0, 18, "Heritage Terminus"),
        Station(41, "Byculla", "BY", "Central", 18.97600, 72.83380, 2.0, 3, 4, "Zoo, Museum"),
        Station(45, "Dadar Central", "DR", "Central", 19.01834, 72.84321, 1.0, 3, 8, "Central Interchange"),
        Station(48, "Kurla", "C", "Central", 19.06560, 72.87910, 1.0, 4, 8, "Harbour Interchange"),
        Station(50, "Ghatkopar", "G", "Central", 19.08600, 72.90800, 2.0, 3, 4, "Metro 1"),
        Station(56, "Thane", "TNA", "Central", 19.18600, 72.97500, 2.0, 4, 10, "Major Junction"),
        Station(61, "Dombivli", "DI", "Central", 19.21830, 73.08600, 1.0, 3, 5, "Suburban Hub"),
        Station(63, "Kalyan Junction", "KYN", "Central", 19.23500, 73.13000, 3.0, 6, 8, "Branching Terminus"),
        // Harbour Line
        Station(88, "Wadala Road", "VDLR", "Harbour", 19.01700, 72.85800, 2.0, 3, 4, "Harbour Hub"),
        Station(95, "Vashi", "VSH", "Harbour", 19.06320, 72.99880, 8.0, 8, 4, "Navi Mumbai Gateway"),
        Station(104, "Panvel", "PNVL", "Harbour", 18.98900, 73.11700, 3.0, 3, 7, "Terminus")
    )
}
