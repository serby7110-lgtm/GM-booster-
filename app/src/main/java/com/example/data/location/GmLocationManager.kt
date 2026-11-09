package com.example.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class UserLocationInfo(
    val latitude: Double = 24.7136,
    val longitude: Double = 46.6753,
    val altitudeMeters: Double = 612.0,
    val speedKmh: Float = 0f,
    val accuracyMeters: Float = 4.5f,
    val bearingDegrees: Float = 0f,
    val locationName: String = "الرياض، المملكة العربية السعودية",
    val isGpsEnabled: Boolean = true,
    val hasPermission: Boolean = true,
    val lastUpdateTimestamp: Long = System.currentTimeMillis()
) {
    val formattedCoordinates: String
        get() = String.format(Locale.US, "%.4f° N, %.4f° E", latitude, longitude)

    val altitudeFormatted: String
        get() = "${altitudeMeters.toInt()} م عن سطح البحر"
}

class GmLocationManager(
    private val context: Context,
    private val scope: CoroutineScope
) : LocationListener {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    private val _currentLocation = MutableStateFlow(UserLocationInfo())
    val currentLocation: StateFlow<UserLocationInfo> = _currentLocation.asStateFlow()

    private var isListening = false

    init {
        checkPermissionAndStartUpdates()
    }

    fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    fun checkPermissionAndStartUpdates() {
        try {
            val hasPerm = hasLocationPermission()
            if (!hasPerm) {
                _currentLocation.value = _currentLocation.value.copy(hasPermission = false)
                return
            }

            _currentLocation.value = _currentLocation.value.copy(hasPermission = true)

            val lm = locationManager ?: return
            val providers = lm.allProviders ?: emptyList()
            val isGpsAvailable = providers.contains(LocationManager.GPS_PROVIDER)
            val isNetworkAvailable = providers.contains(LocationManager.NETWORK_PROVIDER)

            val isGpsEnabled = isGpsAvailable && (try { lm.isProviderEnabled(LocationManager.GPS_PROVIDER) } catch (e: Throwable) { false })
            val isNetworkEnabled = isNetworkAvailable && (try { lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } catch (e: Throwable) { false })

            _currentLocation.value = _currentLocation.value.copy(
                isGpsEnabled = isGpsEnabled || isNetworkEnabled
            )

            // Try getting last known location first
            var lastLoc: Location? = null
            if (isGpsEnabled) {
                lastLoc = try { lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) } catch (e: Throwable) { null }
            }
            if (lastLoc == null && isNetworkEnabled) {
                lastLoc = try { lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } catch (e: Throwable) { null }
            }

            lastLoc?.let { updateWithLocation(it) }

            // Request location updates
            if (!isListening) {
                if (isGpsEnabled) {
                    try {
                        lm.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            3000L,
                            3.0f,
                            this
                        )
                        isListening = true
                    } catch (e: Throwable) {
                        // fallback
                    }
                }
                if (!isListening && isNetworkEnabled) {
                    try {
                        lm.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            4000L,
                            5.0f,
                            this
                        )
                        isListening = true
                    } catch (e: Throwable) {
                        // fallback
                    }
                }
            }
        } catch (e: Throwable) {
            _currentLocation.value = _currentLocation.value.copy(hasPermission = false)
        }
    }

    override fun onLocationChanged(location: Location) {
        scope.launch(Dispatchers.Default) {
            updateWithLocation(location)
        }
    }

    private fun updateWithLocation(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        val speedKmh = location.speed * 3.6f
        val alt = location.altitude
        val acc = location.accuracy
        val bearing = location.bearing

        val placeName = resolvePlaceName(lat, lng)

        _currentLocation.value = UserLocationInfo(
            latitude = lat,
            longitude = lng,
            altitudeMeters = alt,
            speedKmh = (speedKmh * 10).toInt() / 10f,
            accuracyMeters = (acc * 10).toInt() / 10f,
            bearingDegrees = bearing,
            locationName = placeName,
            isGpsEnabled = true,
            hasPermission = true,
            lastUpdateTimestamp = System.currentTimeMillis()
        )
    }

    /**
     * Resolves high-level regional Arabic place names based on coordinates
     */
    fun resolvePlaceName(lat: Double, lng: Double): String {
        return when {
            // Saudi Arabia
            lat in 24.4..25.2 && lng in 46.4..47.2 -> "الرياض، المملكة العربية السعودية 🇸🇦"
            lat in 21.3..21.9 && lng in 39.0..39.4 -> "جدة، المملكة العربية السعودية 🇸🇦"
            lat in 21.2..21.6 && lng in 39.6..40.0 -> "مكة المكرمة 🕋"
            lat in 24.3..24.7 && lng in 39.4..39.8 -> "المدينة المنورة 🕌"
            lat in 26.2..26.6 && lng in 49.9..50.3 -> "الدمام / الخبر 🇸🇦"
            // UAE
            lat in 25.0..25.4 && lng in 55.1..55.5 -> "دبي، الإمارات 🇦🇪"
            lat in 24.3..24.6 && lng in 54.2..54.6 -> "أبوظبي، الإمارات 🇦🇪"
            // Egypt
            lat in 29.8..30.3 && lng in 31.0..31.5 -> "القاهرة، مصر 🇪🇬"
            lat in 31.1..31.4 && lng in 29.8..30.1 -> "الإسكندرية، مصر 🇪🇬"
            // Iraq
            lat in 33.1..33.5 && lng in 44.2..44.6 -> "بغداد، العراق 🇮🇶"
            // Jordan
            lat in 31.8..32.1 && lng in 35.8..36.1 -> "عَمّان، الأردن 🇯🇴"
            // Morocco
            lat in 33.4..33.7 && lng in -7.8..-7.4 -> "الدار البيضاء، المغرب 🇲🇦"
            // Algeria
            lat in 36.6..36.9 && lng in 2.9..3.3 -> "الجزائر العاصمة 🇩🇿"
            // Kuwait
            lat in 29.2..29.5 && lng in 47.8..48.2 -> "مدينة الكويت 🇰🇼"
            // Qatar
            lat in 25.1..25.4 && lng in 51.4..51.7 -> "الدوحة، قطر 🇶🇦"
            else -> String.format(Locale.US, "الموقع الحالي (%.3f° N, %.3f° E)", lat, lng)
        }
    }

    fun stopUpdates() {
        try {
            if (isListening) {
                locationManager?.removeUpdates(this)
                isListening = false
            }
        } catch (e: Exception) {
            // Ignored
        }
    }

    override fun onProviderEnabled(provider: String) {
        _currentLocation.value = _currentLocation.value.copy(isGpsEnabled = true)
    }

    override fun onProviderDisabled(provider: String) {
        _currentLocation.value = _currentLocation.value.copy(isGpsEnabled = false)
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}
