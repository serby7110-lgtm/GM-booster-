package com.example.data.tracking

import com.example.data.database.GmDatabase
import com.example.data.database.GpsTrackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class GpsTrackerEngine(
    private val database: GmDatabase,
    private val scope: CoroutineScope
) {
    private val _session = MutableStateFlow(GpsTrackingSession())
    val session: StateFlow<GpsTrackingSession> = _session.asStateFlow()

    private var trackerJob: Job? = null

    // Base coordinate reference (e.g. Riyadh / Cairo / Dubai)
    private var baseLat = 24.7136
    private var baseLng = 46.6753
    private var headingDegrees = 45.0

    fun startSession(
        type: GpsSessionType,
        targetLimitKm: Float,
        targetSteps: Int
    ) {
        trackerJob?.cancel()

        val isRun = type == GpsSessionType.OUTDOOR_RUN
        val isBike = type == GpsSessionType.CYCLING

        val initialSpeed = when {
            isBike -> 18.5f
            isRun -> 9.5f
            else -> 4.8f
        }

        // Generate initial start coordinate
        val initialCoord = GpsCoordinate(
            latitude = baseLat,
            longitude = baseLng,
            speedKmh = initialSpeed,
            timestamp = System.currentTimeMillis()
        )

        _session.value = GpsTrackingSession(
            type = type,
            state = GpsSessionState.TRACKING,
            distanceKm = 0.05f,
            stepCount = if (isRun) 65 else if (isBike) 12 else 45,
            elapsedSeconds = 0,
            currentSpeedKmh = initialSpeed,
            avgSpeedKmh = initialSpeed,
            currentPaceMinKm = if (initialSpeed > 0.1f) 60f / initialSpeed else 10f,
            caloriesBurned = 5,
            targetDistanceKm = targetLimitKm,
            targetDailySteps = targetSteps,
            pathPoints = listOf(initialCoord),
            isLimitReached = false,
            startTime = System.currentTimeMillis()
        )

        startTrackingLoop()
    }

    private fun startTrackingLoop() {
        trackerJob = scope.launch {
            while (true) {
                delay(1000)
                val current = _session.value
                if (current.state != GpsSessionState.TRACKING) continue

                val newElapsed = current.elapsedSeconds + 1
                val isRun = current.type == GpsSessionType.OUTDOOR_RUN
                val isBike = current.type == GpsSessionType.CYCLING

                // Speed calculation with natural micro-variations
                val baseSpeed = when {
                    isBike -> 19.2f
                    isRun -> 10.2f
                    else -> 5.2f
                }
                val speedVariation = (Random.nextFloat() - 0.5f) * 0.8f
                val currentSpeed = (baseSpeed + speedVariation).coerceAtLeast(1.0f)

                // Distance delta in 1 second
                val distanceIncrementKm = (currentSpeed / 3600f)
                val newDistance = current.distanceKm + distanceIncrementKm

                // Step count increment based on cadence
                val stepCadencePerSec = when {
                    isBike -> (20..30).random() / 60f
                    isRun -> (155..165).random() / 60f
                    else -> (105..115).random() / 60f
                }
                val newSteps = (current.stepCount + stepCadencePerSec).toInt()

                // Pace (min per km)
                val currentPace = if (currentSpeed > 0.1f) 60f / currentSpeed else 12f
                val avgSpeed = (newDistance / (newElapsed / 3600f)).coerceAtLeast(0.1f)

                // Calories burned: ~60 kcal per km for walk, ~75 kcal per km for run, ~45 kcal for cycling
                val calFactor = when {
                    isBike -> 45f
                    isRun -> 75f
                    else -> 58f
                }
                val newCalories = (newDistance * calFactor).toInt().coerceAtLeast(1)

                // Simulate next GPS coordinate waypoint
                headingDegrees += (Random.nextDouble() - 0.5) * 15.0
                val rad = Math.toRadians(headingDegrees)
                val latDelta = (distanceIncrementKm / 111.0) * cos(rad)
                val lngDelta = (distanceIncrementKm / (111.0 * cos(Math.toRadians(baseLat)))) * sin(rad)
                baseLat += latDelta
                baseLng += lngDelta

                val newCoord = GpsCoordinate(
                    latitude = baseLat,
                    longitude = baseLng,
                    speedKmh = currentSpeed,
                    altitudeMeters = (600 + sin(newElapsed * 0.05) * 12).toFloat(),
                    timestamp = System.currentTimeMillis()
                )

                val updatedPoints = current.pathPoints + newCoord

                val limitReached = newDistance >= current.targetDistanceKm || newSteps >= current.targetDailySteps

                _session.value = current.copy(
                    elapsedSeconds = newElapsed,
                    distanceKm = (newDistance * 100).toInt() / 100f,
                    stepCount = newSteps,
                    currentSpeedKmh = (currentSpeed * 10).toInt() / 10f,
                    avgSpeedKmh = (avgSpeed * 10).toInt() / 10f,
                    currentPaceMinKm = (currentPace * 10).toInt() / 10f,
                    caloriesBurned = newCalories,
                    pathPoints = updatedPoints,
                    isLimitReached = limitReached
                )
            }
        }
    }

    fun pauseSession() {
        val current = _session.value
        if (current.state == GpsSessionState.TRACKING) {
            _session.value = current.copy(state = GpsSessionState.PAUSED)
        }
    }

    fun resumeSession() {
        val current = _session.value
        if (current.state == GpsSessionState.PAUSED) {
            _session.value = current.copy(state = GpsSessionState.TRACKING)
        }
    }

    suspend fun finishAndSaveSession(): GpsTrackEntity = withContext(Dispatchers.IO) {
        trackerJob?.cancel()
        val finalSession = _session.value
        _session.value = finalSession.copy(state = GpsSessionState.COMPLETED)

        // Save session locally to Room database
        val entity = GpsTrackEntity(
            sessionType = finalSession.type.name,
            distanceKm = finalSession.distanceKm,
            stepCount = finalSession.stepCount,
            durationSeconds = finalSession.elapsedSeconds,
            avgSpeedKmh = finalSession.avgSpeedKmh,
            paceMinPerKm = finalSession.currentPaceMinKm,
            caloriesBurned = finalSession.caloriesBurned,
            routeCoordinatesJson = serializeRoute(finalSession.pathPoints),
            targetLimitKm = finalSession.targetDistanceKm,
            dateTimestamp = System.currentTimeMillis(),
            isSynced = false // Ready for offline caching and automatic cloud sync
        )

        database.gpsTrackDao().insertGpsTrack(entity)
        entity
    }

    fun resetSession() {
        trackerJob?.cancel()
        _session.value = GpsTrackingSession(state = GpsSessionState.IDLE)
    }

    private fun serializeRoute(points: List<GpsCoordinate>): String {
        return points.joinToString(";") { "${it.latitude},${it.longitude},${it.speedKmh}" }
    }
}
