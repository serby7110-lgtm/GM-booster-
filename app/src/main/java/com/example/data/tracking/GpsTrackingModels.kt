package com.example.data.tracking

enum class GpsSessionType(val titleArabic: String, val titleEnglish: String, val icon: String) {
    OUTDOOR_RUN("الجري والهرولة", "Outdoor Run", "directions_run"),
    OUTDOOR_WALK("المشي السريع في الهواء الطلق", "Outdoor Walk", "directions_walk"),
    CYCLING("ركوب الدراجة الهوائية", "Cycling", "directions_bike"),
    INDOOR_WALK("مشي داخلي / سير كهربائي", "Indoor Walk", "directions_walk")
}

enum class GpsSessionState {
    IDLE,
    TRACKING,
    PAUSED,
    COMPLETED
}

data class GpsCoordinate(
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Float = 0f,
    val altitudeMeters: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

data class GpsTrackingSession(
    val type: GpsSessionType = GpsSessionType.OUTDOOR_RUN,
    val state: GpsSessionState = GpsSessionState.IDLE,
    val distanceKm: Float = 0f,
    val stepCount: Int = 0,
    val elapsedSeconds: Int = 0,
    val currentSpeedKmh: Float = 0f,
    val avgSpeedKmh: Float = 0f,
    val currentPaceMinKm: Float = 0f,
    val caloriesBurned: Int = 0,
    val targetDistanceKm: Float = 5.0f,
    val targetDailySteps: Int = 10000,
    val pathPoints: List<GpsCoordinate> = emptyList(),
    val isLimitReached: Boolean = false,
    val startTime: Long = System.currentTimeMillis()
) {
    val durationSeconds: Int get() = elapsedSeconds
    val avgPaceMinPerKm: Float get() = currentPaceMinKm
    val targetSteps: Int get() = targetDailySteps
    val routeCoordinates: List<GpsCoordinate> get() = pathPoints
    val sessionType: GpsSessionType get() = type
}
